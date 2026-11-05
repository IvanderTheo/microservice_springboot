sekarang pastikan semua service bisa put patch delete.
pastikan patch sifatnya opsional

user service
- name
- email

product service
- name
- description
- stock
- price
- category

order service hanya bisa update quantity dari items saja dan dapat ganti product id semisal item disuatu order adalah 1 dengan quantity 2 maka dapat diganti menjadi product id 1 quantity 2 hanya untuk update.

patch order service hanya bisa menerima perubahan quantity 
untuk json pertahankan 
{
    items: [
        {}
    ]
}


payment hanya bisa delete, tidak bisa patch dan update