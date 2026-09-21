<!-- Mirror of the Claude project doc "claude/app-navigation.md" (taslak v5, 19.09.2026). Update both when decisions change. -->
# Purrello — Uygulama navigasyonu (taslak v5)

Tasarım canvas'ı: https://claude.ai/artifact/3j3CgNvQxrTBGfS7bXas4x

## Tab bar (5 sekme)
Ana sayfa · Sağlık · Belgeler · Bakım · [Pet adı]
- Son sekme seçili petin profili (etiket = pet adı, ikon = tek avatar). Harcama MVP dışı, tab bar'dan çıktı.
- Çoklu pette de tek avatar (yığın/ikili avatar denendi, beğenilmedi, geri alındı).
- Aktif: 56×32 bg-brand-soft hap, ikon + etiket text-link; pet sekmesinde fotoğrafın etrafında 2px action-primary halka
- Detay ve modal ekranlarda gizlenir

## Pet değiştirme
- Görünür yollar: Ana sayfa başlığındaki pet seçici (dropdown, bilerek kaldı), pet profilindeki ad + ▾
- Kısayol: pet sekmesine basılı tut → aynı pet seçme sheet'i (+ haptik)
- İpucu: 2. pet eklendiğinde bir kez, pet sekmesine işaret eden koyu baloncuk ("Anladım"); tek petli kullanıcıya gösterilmez
- Erişilebilirlik: pet sekmesine "Pet değiştir" custom accessibility action
- Sağlık / Belgeler / Bakım'da dropdown yok; başlık alt satırında petin mini avatarı + adı ("Boncuk'un aşı ve sağlık takibi"); kompakt barda solda petin avatarı (tıklanmaz)
- Bildirimden gelinirse ilgili pete otomatik geçilir

## Sahip profili
- Her kök başlıktaki sağ üst avatar → Profil (push)

## Başlıklar
- Sekme kökü (scroll yok): "sıcak başlık" — bg-hero #FCDFDC, alt köşeler 28px, coral-200 pati izleri, başlık 26/34 koyu ink, alt satır #7B3F3A, 48px dolu + butonu
- Sekme kökü (scroll edilmiş): 56px kompakt, aynı renk, alt köşeler 20px + yumuşak gölge, filtre chip'leri içinde sabit
- Beyaz yazı mercan üstünde kullanılmaz (2.3:1)
- Detay: geri + ortalı başlık, şeffaf; scroll'da bg-surface + çizgi. Modal form: × ve altta sabit Kaydet

## Hata gösterimi (tüm uygulama)
- Toast / snackbar kullanılmaz.
- Sistem hataları (bağlantı, sunucu, yükleme başarısız) → popup (alertdialog), "Tekrar dene" popup içinde.
- Form alan hataları (geçersiz kilo, eksik alan) popup değil, alanın altında inline kalır.
- Kullanıcı Google girişinden kendisi vazgeçerse hiçbir şey gösterilmez, login ekranına dönülür.
- Login: bağlantı yok → Tekrar dene / Kapat; hesap/sunucu → Tekrar dene / Destekle iletişime geç / Kapat + hata kodu

## Pet profili
Başlıkta fotoğraf, ad ▾ (pet değiştir), tür/ırk/cinsiyet, Yaş/Kilo/Kısır kutuları, "Acil durum QR" ve "Kayıp bildir" butonları; Kimlik ve pasaport (çip, pasaport no, kopyala, Pasaport görünümü), Sağlık özeti (alerji, kronik, ilaç), Acil durum QR kartı, Resmi belgeler (sertifika bitiş uyarısı), Peti sil

## Ana sayfa
- Hızlı işlemler: Aşı ekle · Belge yükle · Bakım ekle · Kilo gir (Vet QR ana sayfadan kaldırıldı; Belgeler sekmesinde)

## Ekleme akışları (tam ekran modal, × ile kapanır, Kaydet altta sabit)
- Pet ekle: 3 adım — 1) foto, ad, tür; 2) ırk, doğum tarihi, cinsiyet, kısırlaştırma, kilo; 3) çip, pasaport (atlanabilir, pasaport tarama)
- Aşı ekle: Yapıldı/Planla, aşı (tanım listesinden), tarih, sonraki doz (tanımdan otomatik önerilir), veteriner, hatırlatma, aşı kartı fotoğrafı
- Alerji / kronik hastalık: arama sheet'i (autocomplete, gruplu), listede yoksa serbest ekleme, mevcutlar chip
- İlaç: ilaç, doz, sıklık, saatler, başlangıç/bitiş, doz hatırlatması
- Belge: kaynak sheet'i (Fotoğraf çek / Galeri / Dosya / Veterinerden iste) → sayfalar + yükleme durumu, kategori, başlık, tarih, veteriner, not
- Veterinerden iste: QR + kod, 24 saat geçerli, tek kullanımlık anahtarı, paylaş/kopyala/iptal
- Bakım: işlem chip'leri (çoklu), tarih, kuaför/evde, salon, öncesi/sonrası foto, sonraki randevu + hatırlatma

## Pasaport ve acil durum QR
- Pasaport görünümü: pasaport kartı (no, çip, doğum, cinsiyet/kısır, renk, kayıt otoritesi), çevrimdışı rozeti, kuduz geçerliliği, taranmış sayfalar, PDF paylaş
- Acil QR: pete özel, kalıcı (bilgiler değişse de QR aynı; statik link → sunucuda güncel sayfa). Okutan kişi ne görsün anahtarları (ad+foto her zaman; telefon, alerji, ilaç, veteriner opsiyonel). Çıktı: Yazdır PDF (künye 2,5 cm, tasma etiketi, A4 poster), görsel kaydet
- QR okutulunca web sayfası açılır (uygulama gerekmez); kayıp modunda kırmızı banner, "Sahibini ara", "Konumumu sahibine gönder"

## Kayıp bildirimi
- Giriş: pet profilindeki "Kayıp bildir"
- Form: son görüldüğü yer (harita), zaman, ayırt edici bilgiler, telefonu göster anahtarı, 5 km bilgi notu → onay popup'ı → yayınlanır
- Aktif ilan: pet profilinde kırmızı banner (Bulundu / İlanı gör) + "Görenler" listesi
- Yakındaki kullanıcı: push → Kayıp ilanı detayı (foto, ad, tür/ırk/cinsiyet/yaş, mesafe, süre, harita, ayırt edici özellikler, "ürkek olabilir" uyarısı, sahibi adı + baş harf soyadı, ara). Altta sabit: Sahibini ara / Gördüm
- "Gördüm" sheet'i: konum, zaman (Şimdi/15 dk/1 saat), foto, not → yalnızca sahibine gider
- Gizlilik: soyad kısaltılır, adres/çip no gösterilmez, telefon sadece sahip açarsa; "Uygunsuz ilanı bildir" linki; kayıp bildirimleri Profil › Bildirimler'den kapatılabilir

## DS'e eklenecekler
bg-hero / hero-paw / hero-text-2 tokenları, login pati rengi #F5ABA7 (tone-on-tone), sıcak başlık, kompakt başlık, tab bar, pet seçici, dialog, coach mark, switch, segmented control, adım göstergesi, seçim kartları, alt aksiyon barı, harita kutusu, QR kartı, pasaport kartı, uyarı banner'ı, kart, liste satırı, badge, istatistik kutusu, anahtar-değer satırı
