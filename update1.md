saya sudah menyelesaikan api versioning untuk v1. sekarang saya ingin menambahkan api versioning v2 dan perubahan yang ditambahkan cukup nambahin image di database dan sifatnya opsional. sekarang implementasikan api version 2 untuk product service. v1 tetap tanpa image, v2 mengembalikan image path

implementasika image dengan pendekatan local filesystem.

implementasikan abstraction agar mudah migrasi

silahkan update database atau model atau dto. tetap pastikan v1 tidak mengembalikan image sedangkan v2 mengembalikan image.

langsung implementasikan POST, GET, UPDATE, PATCH, DELETE untuk v2

INGAT!!! JANGAN UBAH V1 DAN PASTIKAN V1 DAN V2 BISA DIGUNAKAN.