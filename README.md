# 📚 Booknest – Community-Driven Online Bookstore  

![Spring Boot](https://img.shields.io/badge/Spring%20Boot-6DB33F?logo=springboot&logoColor=white)
![React](https://img.shields.io/badge/React-61DAFB?logo=react&logoColor=black)
![TypeScript](https://img.shields.io/badge/TypeScript-3178C6?logo=typescript&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL-4479A1?logo=mysql&logoColor=white)
![Docker](https://img.shields.io/badge/docker-257bd6?logo=docker&logoColor=white)
![AWS](https://img.shields.io/badge/AWS-232F3E?logo=amazonaws&logoColor=white)
![GitHub Actions](https://img.shields.io/badge/GitHub%20Actions-2088FF?logo=githubactions&logoColor=white)

---

## ✨ What is Booknest?  
**Booknest** is a community-driven online bookstore that blends book discovery, borrowing, and reviews with **author-led communities**.  
Unlike Goodreads or LibraryThing, here authors and readers can interact directly: authors run their own communities, post updates, and fans can comment, like, and get **real-time notifications**.  

---

## 🚀 Features  

### 👤 Basic Users
- 🔍 Search books & authors  
- 📖 Borrow up to **5 books** at a time (3-day loan period)  
- ⭐ Leave reviews & ratings  
- 💬 Join author communities, like & comment on posts  
- 🔔 Get notifications for new posts  

### ✍️ Authors
- 📝 Create **one author community**  
- 📸 Post updates with optional images  
- 💬 Interact with fans in threaded comments  
- 🗑️ Moderate comments on their posts  

### 🛡️ Admins
- 📚 Manage book library (CRUD)  
- 🚨 Oversee communities and remove inappropriate posts/comments

---

## 🛠️ Tech Stack
- **Backend:** Spring Boot, Spring Data JPA, Flyway, JWT with RSA, JUnit tests  
- **Frontend:** React + Vite + TypeScript, React Query, React Router, MUI, Cypress  
- **Database:** MySQL (hosted on AWS RDS)  
- **Cloud:** AWS (S3, RDS, ECR, ECS Fargate, ALB, ACM, Cloudflare DNS)  
- **CI/CD:** GitHub Actions + OIDC for AWS deploy

---

## 🔐 Authentication & Security  
- JWT-based authentication (RSA keypair signing/verification)  
- Role-based access: `USER`, `AUTHOR`, `ADMIN`  
- Email verification with 6-digit codes + scheduled cleanup for unverified accounts  

---

## 📸 Screenshots  
#### System Workflow
![System Workflow](screenshots/Local.png)  

#### Book Details Page
![Book Details Page](screenshots/book-details-page.png)  

#### Search Books Page
![Search Books](screenshots/search-books.png)  

#### Author Community Page
![Author Community](screenshots/author-community.png)  

#### Post with Comments
![Post Example](screenshots/post-example.png)  

#### Notification List
![Notification List](screenshots/notification-list.png)  

---

## 🔄 CI/CD
- Detect changes workflow: only run jobs for changed services
- Backend jobs: build, test, Dockerize, push to ECR, deploy on ECS
- Frontend jobs: build, lint, Cypress tests, deploy to Netlify
- Rollback enabled on deployment failure

---

## ☁️ Deployment
- Backend: AWS ECS (Fargate) + ALB + RDS + S3
- Frontend: Netlify (static hosting over HTTPS)
- Security: AWS IAM roles via OIDC + Security Groups + HTTPS certs via ACM

---

## 🛣️ Future Work
- 🎥 Live author Q&A sessions
- 📱 Mobile app (React Native)
- 📊 Personalized recommendations

---

## 🙌 Acknowledgments
This project was developed as part of my **Bachelor’s Thesis in Computer Science**.
Special thanks to my thesis supervisor, peers, and the open-source community.

## 📄 MIT License
