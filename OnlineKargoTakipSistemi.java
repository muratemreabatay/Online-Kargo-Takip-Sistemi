import java.util.*;

public class OnlineKargoTakipSistemi {

    //  Müşteri Bilgi Yönetimi (Bağlı Liste) 
    static class Gonderi {
        String gonderiID;
        String gonderiTarihi;
        String teslimDurumu;
        int teslimSuresi;
        Sehir sehir;

        Gonderi(String gonderiID, String gonderiTarihi, String teslimDurumu, int teslimSuresi, Sehir sehir) {
            this.gonderiID = gonderiID;
            this.gonderiTarihi = gonderiTarihi;
            this.teslimDurumu = teslimDurumu;
            this.teslimSuresi = teslimSuresi;
            this.sehir = sehir;
        }

        @Override
        public String toString() {
            return "GönderiID: " + gonderiID + ", Tarih: " + gonderiTarihi + ", Durum: " + teslimDurumu + ", Süre: " + teslimSuresi +
                    " gün, Şehir: " + (sehir != null ? sehir.sehirAdi : "Bilinmiyor");
        }
    }

    static class Musteri {
        String musteriID;
        String ad;
        String soyad;
        LinkedList<Gonderi> gonderiGecmisi;

        Musteri(String musteriID, String ad, String soyad) {
            this.musteriID = musteriID;
            this.ad = ad;
            this.soyad = soyad;
            this.gonderiGecmisi = new LinkedList<>();
        }

        void gonderiEkle(Gonderi gonderi) {
            gonderiGecmisi.add(gonderi);
            gonderiGecmisi.sort(Comparator.comparing(g -> g.gonderiTarihi));
        }

        @Override
        public String toString() {
            return "MüşteriID: " + musteriID + ", Ad: " + ad + " " + soyad;
        }
    }

    static class Sehir {
        String sehirAdi;
        String sehirID;
        List<Sehir> altSehirler;

        Sehir(String sehirAdi, String sehirID) {
            this.sehirAdi = sehirAdi;
            this.sehirID = sehirID;
            this.altSehirler = new ArrayList<>();
        }

        void altSehirEkle(Sehir sehir) {
            altSehirler.add(sehir);
        }

        @Override
        public String toString() {
            return sehirAdi + " (" + sehirID + ")";
        }
    }

    static List<Musteri> musteriler = new ArrayList<>();
    static Sehir merkez;

    static void yeniMusteriEkle(Scanner scanner) {
        System.out.print("Müşteri ID girin: ");
        String musteriID = scanner.nextLine();
        System.out.print("Ad girin: ");
        String ad = scanner.nextLine();
        System.out.print("Soyad girin: ");
        String soyad = scanner.nextLine();
        musteriler.add(new Musteri(musteriID, ad, soyad));
        System.out.println("Müşteri başarıyla eklendi!");
    }

    static void musteriGonderiGecmisiGoruntule(Scanner scanner) {
        System.out.print("Gönderi geçmişini görüntülemek için Müşteri ID girin: ");
        String musteriID = scanner.nextLine();
        Musteri musteri = musteriler.stream()
                .filter(m -> m.musteriID.equals(musteriID))
                .findFirst()
                .orElse(null);

        if (musteri == null) {
            System.out.println("Müşteri bulunamadı!");
            return;
        }

        if (musteri.gonderiGecmisi.isEmpty()) {
            System.out.println("Gönderi geçmişi bulunamadı.");
        } else {
            for (Gonderi gonderi : musteri.gonderiGecmisi) {
                System.out.println(gonderi);
            }
        }
    }

    static void musteriyeGonderiEkle(Scanner scanner) {
        System.out.print("Gönderi eklemek için Müşteri ID girin: ");
        String musteriID = scanner.nextLine();
        Musteri musteri = musteriler.stream()
                .filter(m -> m.musteriID.equals(musteriID))
                .findFirst()
                .orElse(null);

        if (musteri == null) {
            System.out.println("Müşteri bulunamadı!");
            return;
        }

        System.out.print("Gönderi ID girin: ");
        String gonderiID = scanner.nextLine();
        System.out.print("Gönderi Tarihi girin (YYYY-AA-GG): ");
        String gonderiTarihi = scanner.nextLine();
        System.out.print("Teslim Durumu girin (Teslim Edildi/Edilmedi): ");
        String teslimDurumu = scanner.nextLine();
        System.out.print("Teslim Süresi girin (gün olarak): ");
        int teslimSuresi = scanner.nextInt();
        scanner.nextLine(); 

        System.out.println("Kargo rotasında şehir seçin:");
        Sehir secilenSehir = sehirSec(scanner, merkez);
        if (secilenSehir == null) {
            System.out.println("Şehir seçimi iptal edildi!");
            return;
        }

        musteri.gonderiEkle(new Gonderi(gonderiID, gonderiTarihi, teslimDurumu, teslimSuresi, secilenSehir));
        System.out.println("Gönderi başarıyla eklendi!");
    }

    static Sehir sehirSec(Scanner scanner, Sehir kokSehir) {
        if (kokSehir.altSehirler.isEmpty()) {
            System.out.print(kokSehir.sehirAdi + " seçilsin mi? (E/H): ");
            String cevap = scanner.nextLine().trim().toLowerCase();
            return cevap.equals("e") ? kokSehir : null;
        }

        System.out.println(kokSehir.sehirAdi + " altındaki şehirler:");
        for (int i = 0; i < kokSehir.altSehirler.size(); i++) {
            System.out.println((i + 1) + ". " + kokSehir.altSehirler.get(i).sehirAdi);
        }
        System.out.print("Seçim yapın (1-" + kokSehir.altSehirler.size() + "): ");
        int secim = scanner.nextInt();
        scanner.nextLine(); 

        if (secim < 1 || secim > kokSehir.altSehirler.size()) {
            System.out.println("Geçersiz seçim!");
            return null;
        }

        return sehirSec(scanner, kokSehir.altSehirler.get(secim - 1));
    }

    static void agacYazdir(Sehir sehir, int seviye) {
        for (int i = 0; i < seviye; i++) System.out.print("--");
        System.out.println(sehir);
        for (Sehir altSehir : sehir.altSehirler) {
            agacYazdir(altSehir, seviye + 1);
        }
    }

    static int agacDerinligiHesapla(Sehir sehir) {
        if (sehir.altSehirler.isEmpty()) {
            return 1;
        }
        int maxDerinlik = 0;
        for (Sehir altSehir : sehir.altSehirler) {
            maxDerinlik = Math.max(maxDerinlik, agacDerinligiHesapla(altSehir));
        }
        return maxDerinlik + 1;
    }

    static void kargoDurumuSorgulama(Scanner scanner) {
        List<Gonderi> teslimEdilmis = new ArrayList<>();
        List<Gonderi> teslimEdilmemis = new ArrayList<>();

        for (Musteri musteri : musteriler) {
            for (Gonderi gonderi : musteri.gonderiGecmisi) {
                if (gonderi.teslimDurumu.equals("Teslim Edildi")) {
                    teslimEdilmis.add(gonderi);
                } else {
                    teslimEdilmemis.add(gonderi);
                }
            }
        }

        teslimEdilmis.sort(Comparator.comparing(g -> g.gonderiID));

        System.out.print("Teslim edilmiş kargo ID'si girin: ");
        String gonderiID = scanner.nextLine();
        Gonderi aramaSonucu = binarySearch(teslimEdilmis, gonderiID);

        if (aramaSonucu != null) {
            System.out.println("Kargo Bulundu: " + aramaSonucu);
        } else {
            System.out.println("Kargo bulunamadı!");
        }

        quickSort(teslimEdilmemis, 0, teslimEdilmemis.size() - 1);

        System.out.println("Teslim Edilmemiş Kargolar:");
        for (Gonderi gonderi : teslimEdilmemis) {
            System.out.println(gonderi);
        }
    }

    static Gonderi binarySearch(List<Gonderi> list, String gonderiID) {
        int left = 0, right = list.size() - 1;
        while (left <= right) {
            int mid = left + (right - left) / 2;
            if (list.get(mid).gonderiID.equals(gonderiID)) {
                return list.get(mid);
            }
            if (list.get(mid).gonderiID.compareTo(gonderiID) < 0) {
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }
        return null;
    }

    static void quickSort(List<Gonderi> list, int low, int high) {
        if (low < high) {
            int pi = partition(list, low, high);
            quickSort(list, low, pi - 1);
            quickSort(list, pi + 1, high);
        }
    }

    static int partition(List<Gonderi> list, int low, int high) {
        Gonderi pivot = list.get(high);
        int i = (low - 1);
        for (int j = low; j < high; j++) {
            if (list.get(j).teslimSuresi < pivot.teslimSuresi) {
                i++;
                Collections.swap(list, i, j);
            }
        }
        Collections.swap(list, i + 1, high);
        return i + 1;
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        merkez = new Sehir("Merkez", "M0");
        Sehir sehir1 = new Sehir("İstanbul", "S1");
        Sehir sehir2 = new Sehir("Ankara", "S2");
        Sehir sehir3 = new Sehir("İzmir", "S3");
        Sehir altSehir1 = new Sehir("Kadıköy", "S1.1");
        Sehir altSehir2 = new Sehir("Çankaya", "S2.1");

        merkez.altSehirEkle(sehir1);
        merkez.altSehirEkle(sehir2);
        merkez.altSehirEkle(sehir3);
        sehir1.altSehirEkle(altSehir1);
        sehir2.altSehirEkle(altSehir2);

        String[] secenekler = {"Yeni Müşteri Ekle", "Gönderi Geçmişini Görüntüle", "Gönderi Ekle", "Kargo Rotalama Görüntüle", "Kargo Durumu Sorgula", "Çıkış"};

        while (true) {
            System.out.println("Bir seçenek seçin:");
            for (int i = 0; i < secenekler.length; i++) {
                System.out.println((i + 1) + ". " + secenekler[i]);
            }

            int secim = scanner.nextInt();
            scanner.nextLine();

            switch (secim) {
                case 1 -> yeniMusteriEkle(scanner);
                case 2 -> musteriGonderiGecmisiGoruntule(scanner);
                case 3 -> musteriyeGonderiEkle(scanner);
                case 4 -> {
                    System.out.println("Kargo Rotalama:");
                    agacYazdir(merkez, 0);
                    System.out.println("Ağacın derinliği: " + agacDerinligiHesapla(merkez));
                }
                case 5 -> kargoDurumuSorgulama(scanner);
                case 6 -> {
                    System.out.println("Sistemden çıkılıyor. Hoşça kalın!");
                    scanner.close();
                    return;
                }
                default -> System.out.println("Geçersiz seçim, tekrar deneyin.");
            }
        }
    }
}