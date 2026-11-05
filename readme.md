saya sudah implementasi microservice untuk order, payment, product dan user. lanjut ke konektivitas antar api. buat api dapat berkomunikasi dalam memproses data. 


user service
-  implementasi jwt login pada semua service

product service
- master data dari product yang akan menyimpan identitas product beserta stock barang

order service
- menyimpan riwayat order
- lakukan revisi pada database agar menyimpan id dari user.
- buat post order dapat menerima array dan hanya menerima product id dari product service serta quantity

payment service
- melakukan pembayaran dari order service yang statusnya pending
- revisi pada database agar menyimpan id dari user

## Kontrak API

Endpoint terlindungi pada semua service menggunakan token JWT `Bearer` yang diterbitkan oleh user service. Pendaftaran/login dan pembacaan katalog produk serta dokumentasi API bersifat publik. Salin `.env.example` menjadi `.env` pada setiap service, lalu isi nilai `JWT_SECRET` yang sama (minimal 32 karakter acak) di user, product, order, dan payment. Jangan gunakan nilai contoh pada deployment.

Gateway berjalan pada port `8080`; user, product, order, dan payment masing-masing pada port `8081`, `8082`, `8083`, dan `8084`.

1. Daftarkan akun dengan `POST /auth/register`:

   ```json
   {"name":"Nama","email":"user@example.com","password":"password-aman"}
   ```

2. Login dengan `POST /auth/login` dan gunakan `accessToken` pada header `Authorization: Bearer <token>` untuk endpoint yang memerlukannya.
3. Buat order dengan `POST /api/v1/orders`:

   ```json
   {"items":[{"productId":1,"quantity":2},{"productId":3,"quantity":1}]}
   ```

   Order service mengambil harga dan stok dari product service, menolak product yang tidak ditemukan atau jumlah yang melebihi stok, lalu menyimpan detail item dan total sebagai snapshot pada riwayat order.
4. Bayar order berstatus `PENDING` dengan `POST /api/v1/payment`:

   ```json
   {"orderId":1,"paymentMethod":"transfer","notes":"opsional"}
   ```

   Jumlah pembayaran dihitung dari grand total order (bukan input klien). Setelah berhasil, status pembayaran dan order berubah menjadi `PAID`. Pembayaran dan riwayat order hanya dapat dibaca oleh pemiliknya.

Akun baru memiliki role `GUEST`. Operasi administrasi user dan perubahan master product memerlukan role `ADMIN`; operator dapat memberikan role tersebut melalui database user service untuk akun administrator yang dikelola.