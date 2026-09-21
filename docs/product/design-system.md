<!-- Mirror of the Claude project doc "claude/design-system-decisions.md" (19.09.2026). Update both when decisions change. -->
# Purrello — Design System kararları

Canlı design system: https://claude.ai/code/artifact/fccab519-c0ad-4f85-ab94-61edfa2fc024 (güncelleme: 19.09.2026, uygulama taslağından 20 komponent eklendi)

## Marka
- Ad: **Purrello** · Logo: PURRELLO, Poppins Light 300, harf aralığı 0.3em
- Mercan + pati izi karşılama anlarında (splash, login, onboarding, boş durumlar); sekme ana ekranlarında sıcak başlık (bg-hero). Sağlık kayıtları ve formlar sade.
- Dil: Türkçe, samimi "sen"; login tek dilde ("Hoş geldin", "Google ile devam et").

## Renkler (light / dark)
| Token | Light | Dark |
| --- | --- | --- |
| bg-page | #F7F9FB | #171213 |
| bg-surface | #FFFFFF | #211B1B |
| bg-subtle | #EEF2F6 | #2B2423 |
| bg-brand (hero) | #F08F8A | #A85550 (seçenek A: kısık mercan) |
| bg-brand-soft | #FEF1F0 | #3A2524 |
| bg-hero (sıcak başlık) | #FCDFDC | #3A2524 |
| hero-text-2 | #7B3F3A | #D9B5B1 |
| hero-paw | #F8C2BE | #4A2E2C |
| bg-inverse (coach mark) | #2F3B4C | #F3EEEC |
| action-primary | #C94A43 | #F4A29D |
| text-primary | #2F3B4C | #F3EEEC |
| text-secondary | #5F6F84 | #B3A8A5 |
| paw-print (login patileri) | #F5ABA7 (ton-sür-ton) | #B86A65 |
- Koyu tema sıcak nötrler kullanır (mavimsi gri değil).
- Durum renkleri: success / warning / danger, her zaman ikon + yazıyla.

## Tipografi — Poppins
wordmark 40/48 · display 32/40 · title-1 24/32 · title-2 20/28 · title-3 17/24 · body-lg 16/24 · body 15/22 · body-sm 14/20 · button 16/24 · label 14/20 · caption 12/16 · micro 11/14 · amount 28/36

## Ölçüler
- Boşluk: 4px ızgara, ekran kenarı 24
- Köşe: buton/chip hap, input 12, kart 16, bottom sheet 24, sıcak başlık alt köşeler 28 (kompakt 20)
- Yükseklik: 52 (buton/input), 44 (min dokunma), 36 (chip/küçük buton)

## Komponentler (26)
- Marka: BrandHero
- Navigasyon: AppHeader (genişlemiş + kompakt), TabBar, DetailBar
- Eylemler: Button, BottomActionBar
- Girdiler: TextField, Dropdown, FilterChip, FileUpload, Switch, SegmentedControl, ChoiceCards, StepIndicator, PetSwitcher
- Geri bildirim: Dialog, Banner, CoachMark, Badge
- Kapsayıcılar: ListRow, BottomSheet
- Veri gösterimi: StatTile, KeyValueRow, PassportCard, QRCard, MapPreview

## Kurallar
- Toast / snackbar yok; sistem hataları Dialog (tekrar dene içinde), alan hataları inline, Google girişinden vazgeçme sessiz.
- Beyaz yazı mercan üstünde yok (logo hariç).
- QR kart her iki temada beyaz zemin.

## Açık noktalar
- İkon seti: Phosphor Regular önerildi
- Google "G" resmi asset ile değiştirilecek
- Kayıp ilanı harita pini ~200 m kaydırma kararı bekliyor
