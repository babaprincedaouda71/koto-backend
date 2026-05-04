# Koto Backend — Contexte

## Projet
Koto est un SaaS de gestion de tontine pour l'Afrique de l'Ouest.
Stack : Spring Boot 3.5.14, PostgreSQL, Flyway, JWT, Java 21.

## Structure
- com.koto.auth — register/login/JWT
- com.koto.user — entité User
- com.koto.groupe — CRUD groupes, lien invitation
- com.koto.membre — rejoindre un groupe, rotation
- com.koto.cycle — démarrage cycle, génération paiements automatique
- com.koto.paiement — confirmer paiement, impayés, rappels
- com.koto.rappel — traçage rappels WhatsApp
- com.koto.shared — BaseEntity, ApiResponse, GlobalExceptionHandler
- com.koto.config — SecurityConfig (CORS + JWT), CorsConfig

## Base de données
PostgreSQL local via Docker — port 5432
Migrations Flyway dans src/main/resources/db/migration/
V1 — schéma initial (6 tables)
V2 — cree_le sur membres
V3 — cree_le sur cycles, paiements, rappels

## Ce qui est fait
- Auth complet avec JWT
- CRUD groupes avec token invitation
- Membres rejoignent via token
- Cycles mensuels avec génération automatique des paiements
- Confirmation paiement par l'admin
- Rappels WhatsApp (lien pré-rempli)
- CORS configuré pour localhost:5173

## Ce qui reste à faire
- Page membre (vue simplifiée sans actions admin)
- Variables d'environnement pour le déploiement
- Déploiement Railway