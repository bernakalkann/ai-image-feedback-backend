# AI Image Feedback Backend

[![Node.js](https://img.shields.io/badge/Node.js-v18+-339933?style=for-the-badge&logo=node.js&logoColor=white)](https://nodejs.org/)
[![Express.js](https://img.shields.io/badge/Express.js-4.x-000000?style=for-the-badge&logo=express&logoColor=white)](https://expressjs.com/)
[![MongoDB](https://img.shields.io/badge/MongoDB-Database-47A248?style=for-the-badge&logo=mongodb&logoColor=white)](https://www.mongodb.com/)

Bu proje, kullanıcıların görsel yükleyebildiği, analiz ettirebildiği ve yapay zeka destekli geri bildirimler alabildiği bir sistemin **Backend (Sunucu)** tarafıdır. RESTful API mimarisi ile kurgulanmış olup, veritabanı yönetimi için MongoDB kullanmaktadır.

---

## 🚀 Özellikler

* **🔐 Kimlik Doğrulama:** JWT tabanlı güvenli kayıt ve giriş işlemleri bulunacaktır.
* **📂 Görsel Yönetimi:** Görsel yükleme (Upload) ve dosya yönetimi altyapısı.
* **🤖 AI Entegrasyonu:** Görüntü işleme ve AI servisleri ile iletişim katmanı.
* **💾 Veritabanı Kaydı:** Kullanıcı verileri, görsel meta verileri ve geri bildirimlerin saklanması.
* **🛡️ Güvenlik:** Helmet, CORS ve Rate Limiting ile güçlendirilmiş API güvenliği kullanılacaktır.

---

## 🏗️ Mimari ve Görseller

Projenin sistem akışı aşağıdaki gibidir.
![diyagram1](https://github.com/user-attachments/assets/5aa4cb79-580f-4299-86af-2efd1b863cff)
![diyagram2](https://github.com/user-attachments/assets/9a95a9c4-d9e9-45c3-988d-a4bfbdcf6fbb)

---

## 💻 Kurulum ve Çalıştırma
### 1. Repoyu Klonlayın
```bash
git clone [https://github.com/bernakalkann/ai-image-feedback-backend.git](https://github.com/bernakalkann/ai-image-feedback-backend.git)
cd ai-image-feedback-backend
