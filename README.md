# Popcornium - wirtualny doradca filmowy

<br>

**Krzysztof Tokarski**  
**Marek Karpiński**  
**Bartosz Warda**

<br>

Warszawa  
Styczeń 2026

# 1. Wprowadzenie – Marek
## 1.1 Opis problemu i motywacja

Współczesne aplikacje filmowe oferują dostęp do dużej ilości danych, jednak interakcja z nimi najczęściej ogranicza się do statycznego przeglądania list, filtrowania lub ręcznego wyszukiwania informacji. Użytkownik, który chce uzyskać odpowiedzi na bardziej złożone pytania — np. dotyczące powiązań pomiędzy filmami, aktorami i reżyserami — musi samodzielnie analizować wiele źródeł.

Celem projektu Popcornium było stworzenie systemu, który umożliwia:

eksplorację bazy filmowej w naturalny sposób,

zadawanie pytań w języku naturalnym,

wykorzystanie nowoczesnych technik Retrieval-Augmented Generation (RAG),

porównanie podejścia wektorowego oraz grafowego (GraphRAG) w praktycznym zastosowaniu.

## 1.2 Wizja systemu Popcornium

Popcornium to aplikacja webowa, która łączy klasyczną aplikację filmową z inteligentnym chatbotem opartym o modele językowe. System udostępnia:

listę filmów wraz z grafikami,

szczegóły filmu (obsada, reżyser, opis),

możliwość tworzenia własnych watchlist,

ocenianie filmów za pomocą reakcji - łapka w górę / łapka w dół,

czat kontekstowy wykorzystujący RAG i GraphRAG.

Chatbot uwzględnia zarówno dane domenowe (filmy, aktorzy, reżyserzy), jak i kontekst użytkownika (historia rozmowy, oceny, watchlisty), co pozwala generować bardziej trafne i spersonalizowane odpowiedzi.

## 1.3 Zastosowane technologie
### Backend

Java 21

Spring Boot 3.5.8

Spring Security (stateless, JWT)

PostgreSQL + PgVector

Neo4j

### Frontend

React.js

Integracje zewnętrzne

IMDb – źródło danych filmowych

Wikipedia – dodatkowe informacje opisowe

Azure OpenAI – generowanie embeddingów oraz odpowiedzi LLM

MinIO – przechowywanie zdjęć filmów i aktorów

Infrastruktura

Docker / Docker Compose

pełna konteneryzacja aplikacji i usług

# 2. Opis architektury systemu – Marek

System Popcornium został zaprojektowany w architekturze modułowej, umożliwiającej niezależny rozwój poszczególnych komponentów oraz łatwą integrację nowych źródeł danych.

## 2.1 Ogólny schemat architektury

(diagram przepływu danych i komponentów)

## 2.2 Źródła danych

IMDb – dane filmowe (tytuły, obsada, reżyserzy, daty produkcji)

Wikipedia – opisy i artykuły uzupełniające

MinIO – obrazy filmów i aktorów

Dane użytkownika:

watchlisty

oceny - łapka w górę / łapka w dół

historia konwersacji

## 2.3 Moduły systemu
Data Ingestion

Moduł odpowiedzialny za pobieranie i inicjalne ładowanie danych filmowych do relacyjnej bazy danych. Dane są mapowane na encje domenowe oraz zapisywane przy starcie aplikacji.

Relacyjna baza danych (PostgreSQL + PgVector)

przechowuje dane filmowe prezentowane w interfejsie,

przechowuje embeddingi wykorzystywane w Vector RAG,

zawiera dane użytkowników, ich oceny oraz watchlisty.

Baza grafowa (Neo4j)

Odpowiada za reprezentację relacji pomiędzy encjami:

film → aktor

film → reżyser

film → kategoria

Dane grafowe wykorzystywane są w mechanizmie GraphRAG.

Conversation Agent

Centralny komponent systemu odpowiedzialny za:

analizę intencji zapytania użytkownika,

wybór trybu wyszukiwania (Vector RAG lub GraphRAG),

wzbogacanie promptu o kontekst użytkownika,

komunikację z Azure OpenAI.

Frontend Layer

Aplikacja frontendowa napisana w React.js, umożliwiająca:

przeglądanie filmów,

interakcję z chatbotem,

zarządzanie watchlistami,

ocenianie filmów.

# 3. Embeddings – Marek

System Popcornium wykorzystuje embeddingi do realizacji wyszukiwania semantycznego w ramach podejścia Vector RAG.

## 3.1 Model danych

Główne encje wykorzystywane w procesie generowania embeddingów:

Movie

Actor

Director

Category

Description

WikipediaArticle

MovieRating

Embedding

## 3.2 Proces generowania embeddingów

Proces generowania embeddingów realizowany jest przez klasę EmbeddingService.

System pobiera wszystkie filmy wraz z powiązanymi encjami z relacyjnej bazy danych.

Dane każdego filmu są dzielone na logiczne fragmenty (chunking):

Metadata – tytuły, rok produkcji, reżyser, kategorie

Cast – lista aktorów

Plot summary – opis filmu

Wikipedia article – treść artykułu

Każdy fragment jest przekazywany do integracji Azure OpenAI, gdzie generowany jest embedding.

Wygenerowane wektory zapisywane są w encji Embedding wraz z typem fragmentu i jego treścią.

Embeddingi wykorzystywane są w mechanizmie wyszukiwania kontekstowego podczas obsługi zapytań użytkownika.

## 3.3 Wykorzystanie kontekstu użytkownika

Dane o interakcjach użytkownika:

oceny - łapka w górę / łapka w dół,

watchlisty,

historia rozmów,

są wykorzystywane do wzbogacania zapytań przekazywanych do modelu językowego, co pozwala na generowanie bardziej spersonalizowanych odpowiedzi.

# 4. Graf wiedzy - Bartosz

# 5. Dostępne typy zapytań - Krzysztof 

# 6. Mechanizm obsługi zapytań - Krzysztof

# 7. Wyniki eksperymentów - Bartosz

# 8. Wnioski i rekomendacje - Bartosz

# 9. Instrukcja uruchomienia - Krzysztof


