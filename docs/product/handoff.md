<!-- Copied from SweatIn/functions/Purello_Handoff.md (v2.0, 19.09.2026). Product background; MVP scope decisions in app-navigation.md take precedence (e.g. expenses are out of MVP). -->
# 🐾 Pet Takip Uygulaması — Handoff Dokümanı

**Versiyon:** 2.0
**Tarih:** 19 Eylül 2026
**Amaç:** Mevcut uygulamanın feature envanteri + kişisel pet takip deneyimini genişletecek iyileştirmeler

---

## 1. Proje Özeti

Kullanıcıların sahip oldukları evcil hayvanların sağlık ve bakım durumunu takip etmesini sağlayan bir mobil uygulama.

**Ürün karakteri:** Kişisel pet takip aracı. Sosyal ağ veya topluluk platformu **değildir**. Odak; ağırlıklı olarak **veteriner sonuçları**, **aşı takibi**, ek olarak **kuaför/bakım** bilgilerinin merkezi, kalıcı ve kolay erişilebilir bir dijital dosyada tutulmasıdır.

**Hedef:** Uygulamayı "her şey burada" bir kişisel pet dosyası haline getirmek — pasaport bilgileri, tıbbi belgeler (röntgen/kan sonuçları dahil), aşı, kuaför geçmişi ve tüm harcamalar tek yerde. Veterinerin de bu dosyaya doğrudan katkı yapabilmesini sağlamak.

---

## 2. Mevcut Özellikler (Modül Bazında)

### 🐾 Pet Yönetimi
- Pet ekleme
- Pet listesi (kullanıcının tüm pet'leri)
- Pet detay
- Pet güncelleme
- Pet silme
- Tür listesi
- Irk arama

### 💉 Aşı Takibi
- Aşı tanım listesi (kuduz, karma vb. + yaş aralığı)
- Aşı kaydı ekleme (pet bazlı, tarihli)
- Pet'in tüm aşılarını listeleme
- Aşı kaydı detay
- Aşı kaydı güncelleme
- "Yapıldı" olarak işaretleme
- **Otomatik hatırlatma:** Günde 09:00 UTC cron job (`VaccinationReminderJob`) — yaklaşan/geciken aşılar için FCM push

### 🤧 Alerji Yönetimi
- Alerji tanımları
- Alerji arama (autocomplete)
- Pet'e alerji ekleme
- Pet'in tüm alerjileri
- Alerji silme

### 🩺 Kronik Hastalık
- Hastalık tanımları + arama
- Ekle / Listele / Sil

### 💊 Düzenli İlaç
- İlaç tanımları + arama
- Ekle / Listele / Sil

### 📁 Sağlık Dökümanları (Vault)
- Doküman yükleme — GCS'e kayıt, private bucket
- Doküman listesi — kategori bazlı
- Doküman indirme — 1 saatlik signed URL, ownership check
- Doküman silme
- Depolama kullanımı — kotanın ne kadarı dolu

### 🏠 Profile / Dashboard
- Profil özeti — pet listesi + medical info aggregate
- Dashboard — aktif pet için aşı/sağlık özeti
- Avatar yükleme — GCS'e

### 📍 Konum & Kayıp Pet
- Konum güncelleme — kullanıcının lat/lng'i
- Kayıp pet bildirimi — yakındaki kullanıcılara FCM broadcast

### 🔔 Push Notification
- FCM token kaydı — cihaz ID'sini kullanıcıya bağlar
- Bildirim tercihleri — kayıp pet, aşı hatırlatma vb. açma/kapama

---

## 3. Improvements & Recommendations

### 3.1 🪪 Pet Pasaportu & Kimlik Modülü

Pet'in resmi kimlik bilgilerini uygulamada eksiksiz tutmak — sınır geçişi, otel giriş kaydı, vet ziyareti gibi resmi işlemlerde tek elden erişim.

**Veri alanları:**
- Pasaport numarası & veriliş tarihi
- Chip (microchip) numarası, yerleştirme tarihi, yerleştiren veteriner
- Küpe/tattoo numarası (varsa)
- Kayıt otoritesi / belediye kaydı
- Cinsiyet, kısırlaştırma bilgisi & tarihi
- Renk, ayırt edici özellikler
- Doğum belgesi / soy kütüğü (pedigree) belgesi görüntüsü
- Sahiplik belgesi görüntüsü
- Uluslararası sağlık sertifikaları (seyahat için)

**Fonksiyonlar:**
- Belge görüntüsü yükleme (chip belgesi, pasaport sayfaları, pedigree vb.) — mevcut GCS altyapısı üzerinden
- Pasaport sayfası formatında görüntüleme (offline erişim için önbelleğe)
- Chip numarası ile hızlı arama / kayıp durumunda ID kart oluşturma
- Belge sona erme uyarıları (uluslararası sağlık sertifikası vb.)

### 3.2 🩻 Veteriner Web Portalı — Sürükle-Bırak Belge Yükleme

Mevcut sistem yalnızca kullanıcının kendi belge yüklemesine izin veriyor. **Kritik geliştirme:** Veterinerin kendi elindeki röntgen, kan tahlili, ultrason gibi çıktıları doğrudan pet'in dosyasına yükleyebileceği bir web arayüzü.

**Akış:**
1. Kullanıcı vet ziyaretinde vet'e tek kullanımlık davet linki oluşturur (uygulamadan QR / link üretilir)
2. Vet bu link ile web portalına giriş yapar — hesap açmasına gerek yok
3. Vet, laboratuvar sonuçlarını / röntgen görüntülerini sürükle-bırak ile yükler
4. Kategori seçer (kan tahlili, röntgen, ultrason, biyopsi, konsültasyon notu vb.)
5. Kısa not ekleyebilir (opsiyonel)
6. Yükleme kullanıcının vault'una düşer, kullanıcıya push bildirim gider
7. Link belirli sürede (örn. 24 saat) veya belge yüklendikten sonra otomatik geçersiz olur

**Teknik notlar:**
- Mevcut GCS bucket + signed URL pattern'i yükleme yönünde de kullanılabilir (signed upload URL)
- Ownership check tersine çalışır: pet ID + davet token'ı doğrulanır
- Rate limiting ve dosya boyutu / tipi validasyonu kritik
- DICOM formatı desteği (röntgen için) — düz JPEG/PNG önizleme üretimi gerekli
- Vet tarafında hesap gerekmemesi UX açısından belirleyici — vet'in yeni bir platforma kayıt olması engeldir

**Faz 2 opsiyonu:** Vet hesap oluşturursa kalıcı iş birliği — birden fazla pet'e erişim, geçmiş yüklemeler, klinik logosu.

### 3.3 ✂️ Kuaför / Bakım Modülü

Aşı modülü ile simetrik yapı — kuaför de düzenli tekrar eden bir bakım işlemi.

**Fonksiyonlar:**
- Kuaför ziyareti kaydı (tarih, yer, yapılan işlem)
- İşlem tipleri (tam tıraş, sanitary, tırnak, banyo, kulak temizliği, anal bez, diş bakımı)
- Sonraki randevu tarihi (opsiyonel hatırlatma)
- Kuaförün adı/salon bilgisi
- Ücret alanı (aşağıdaki harcama modülüne besleme)
- Fotoğraf ekleme (öncesi/sonrası)
- Notlar (özel şampuan, davranış, tercih)
- Hatırlatma bildirimi (aşı gibi cron job entegre edilebilir — örn. `GroomingReminderJob`)

**Not:** Aynı pattern ile diş temizliği, parazit uygulaması (iç/dış), tırnak kesimi gibi rutin bakım işlemleri de aynı modülün alt kategorileri olabilir. "Rutin Bakım" adı altında birleştirmek düşünülebilir.

### 3.4 💰 Harcama Takibi Modülü

Kullanıcının tüm pet harcamalarını tek yerde görebilmesi — hem kişisel bütçe planlaması hem de sigorta önerisinin veri kaynağı.

**Veri modeli:**
- Harcama kaydı: tutar, tarih, kategori, pet, açıklama, opsiyonel fiş fotoğrafı
- Kategoriler: Veteriner, Kuaför, Mama, İlaç, Aşı, Ekipman, Sigorta, Diğer
- Otomatik besleme: aşı/vet ziyareti/kuaför modüllerinden ücret alanları

**Görselleştirmeler:**
- Aylık toplam (bu ay / geçen ay karşılaştırma)
- Yıllık toplam
- Kategori bazlı dağılım (pie/bar chart)
- Pet bazlı dağılım (birden fazla pet varsa)
- Trend grafiği (son 12 ay)
- Yıllık özet: "2026'da Boncuk için toplam 12.400 TL harcadın"

**Fonksiyonlar:**
- Manuel harcama ekleme
- Fiş fotoğrafı ekleme (GCS)
- CSV/PDF olarak dışa aktarma (muhasebe/paylaşım için)
- Filtreleme (tarih aralığı, kategori, pet)

### 3.5 🛡️ Pet Sağlık Sigortası Önerisi (Monetization)

Harcama verisi + sağlık verisi birleşerek kullanıcıya değer katan, aynı zamanda affiliate gelir sağlayan bir mekanizma.

**Konumlandırma:** Reklam banner'ı değil, **kullanıcının kendi verisinden çıkan içgörü**. Bu ayrım kritik; UX'i bozmaz, güven kaybettirmez.

**Tetikleyici senaryolar:**
- Yıllık vet harcaması belirli eşiği geçtiğinde (örn. 6.000 TL+)
- Kronik hastalık kaydı eklendiğinde (uzun vadeli maliyet uyarısı)
- Ameliyat/beklenmedik büyük harcama kaydı girildiğinde
- Yaşlı pet (7+) profil oluşturulduğunda

**Öneri formatı örneği:**
> 📊 Bu yıl Boncuk için 8.400 TL veteriner harcaman oldu. Popüler pet sigortaları yıllık ~4.200 TL. İncelemek ister misin?
> [Sigortaları karşılaştır] [Şimdi değil] [Bir daha gösterme]

**Kullanıcının kabul ettiği aşamada:**
- Pet'in ırk, yaş, kronik hastalık bilgileri (kullanıcı onayı ile) sigorta şirketine ön-doldurulmuş form olarak iletilir
- Kullanıcı 3-4 sigorta seçeneğini tek ekranda karşılaştırır
- Affiliate model: satış başına komisyon
- Kullanıcıya özel iskonto pazarlığı yapılabilir (bulk anlaşma)

**Etik / güven notları:**
- Kullanıcı bir kez "gösterme" derse tekrar önerilmez
- Sağlık verisi hiçbir zaman kullanıcı onayı olmadan üçüncü tarafa gitmez
- Ayarlar → Öneriler bölümünden tümüyle kapatılabilir
- Şeffaf olarak "bu bir sponsorlu öneridir" etiketi

**Genişleme fikirleri (aynı içgörü mantığı):**
- Kronik hastalık için özel diyet mama önerisi
- Yaşlı pet için ek besin takviyesi önerisi
- Alerji verisine göre güvenli mama filtresi
- Bunların hepsi affiliate model ile monetize edilebilir; ana ürünü ücretsiz tutmayı sürdürür

### 3.6 Diğer İyileştirme Fırsatları

Yukarıdakiler ana istenen paket. Sağlık odaklı kimliğe uyan ve düşük efor / yüksek değer olan ek fikirler:

- **Ağırlık & vital takibi** — kilo, sıcaklık, nabız trendi grafiği. Kronik hastalık takibinde kritik.
- **İlaç dozu hatırlatma** — düzenli ilaç modülü var, saat bazlı dozaj bildirimi eklenebilir (örn. günde 2 kez, 08:00 & 20:00)
- **Vet ziyareti kaydı** — vet, tarih, sebep, teşhis, tedavi, sonraki kontrol tarihi. Şu an bu ayrı bir modül olarak yok, sadece belgeler var.
- **Semptom takibi** — kısa notlar & fotoğraflarla belirti günlüğü (örn. "3 gündür kulak kaşıyor")
- **Aile / co-owner erişimi** — bir pet'i eş / çocuk / pet sitter ile paylaşabilme. Fine-grained: kim ne görsün seçilebilir.
- **QR kod acil durum kartı** — tasma üzerine yapıştırılabilecek QR; okunduğunda pet adı, sahibin iletişim numarası, alerjiler, kullandığı ilaçlar gösterilir (kayıp durumu için altın değerinde)
- **Pet doğum günü / adopt yıl dönümü** hatırlatması

---

## 4. Teknik Not — Mevcut Altyapının Yeniden Kullanımı

Yeni modüller sıfırdan yazılmayacak, mevcut altyapıyı yeniden kullanacak:

- **GCS medya yönetimi + signed URL pattern** → pasaport belgeleri, vet portal upload, kuaför fotoğrafları, fiş fotoğrafları
- **Ownership check pattern** → pet-doküman ilişkisi zaten var; vet portal için token bazlı geçici erişim varyasyonu eklenecek
- **FCM push altyapısı** → kuaför hatırlatma, ilaç dozu, belge yüklendi bildirimi, sigorta öneri push'u
- **Cron job pattern (`VaccinationReminderJob`)** → `GroomingReminderJob`, `MedicationDoseJob`, `ExpenseWeeklyDigestJob` aynı yapıda yazılabilir
- **Bildirim tercihleri modeli** → yeni event türleri (kuaför, ilaç dozu, öneri) için hazır çerçeve
- **Kategori bazlı doküman listesi** → pasaport belgeleri için yeni kategori seti eklemek yeterli

---

## 5. Sonraki Adımlar

- [ ] Pet Pasaportu modülü için data model tasarımı (alanlar, zorunluluklar, ülkeye göre farklılıklar)
- [ ] Veteriner web portalı için ayrı repo / subdomain planlaması (`vet.appadi.com` gibi)
- [ ] Signed upload URL akışı için güvenlik değerlendirmesi (rate limit, file type whitelist, virus scan)
- [ ] Kuaför/Bakım modülünün rutin bakım şemsiyesi altında birleşik mi ayrı mı tutulacağının kararı
- [ ] Harcama modülü için para birimi, KDV, çoklu döviz desteği kararı
- [ ] Sigorta önerisi için Türkiye pazarındaki potansiyel affiliate partner listesinin çıkarılması
- [ ] Sigorta önerisinin ne sıklıkta ve hangi koşullarda tetikleneceğinin UX kuralları (spam algısı oluşmaması için)
- [ ] Pet pasaportu belge yüklemesi için OCR ile otomatik alan doldurma fizibilitesi
- [ ] DICOM formatı desteği için görüntüleyici kütüphane araştırması (vet portal için)