# 🍿Popcornium - wirtualny doradca filmowy

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

## 4.1. Schemat Grafu

Schemat grafu stanowi fundament całej struktury. Definiuje on typy encji, które reprezentują kluczowe obiekty w domenie, oraz typy relacji, które opisują semantyczne powiązania między tymi obiektami. Poniżej przedstawiony jest graf wygenerowany w aplikacji Neo4j:

[Neo4j Graph](documentation/neo4j_graph.svg)

### 4.1.1. Encje

**Movie**: Jest to centralny węzeł w grafie, stanowiący punkt wyjścia do nawigacji po powiązanych danych. Każdy węzeł `Movie` reprezentuje pojedynczy film i przechowuje jego kluczowe atrybuty, takie jak: unikalny identyfikator, tytuł oryginalny i polski (`originalTitle`, `polishTitle`), rok produkcji (`productionYear`), uśredniona ocena (`rating`), liczba oddanych głosów (`ratingCount`) oraz adres URL do plakatu (`posterUrl`).

**Actor**: Węzeł ten reprezentuje aktora. Każdy aktor jest unikalną encją w grafie, identyfikowaną przez swoje imię i nazwisko (`name`). Dzięki temu modelowi możliwe jest tworzenie sieci powiązań między aktorami a filmami, co pozwala na realizację zapytań typu "znajdź wszystkie filmy, w których wystąpił dany aktor" lub "znajdź aktorów, którzy najczęściej grali razem".

**Director**: Węzeł reprezentujący reżysera filmu. Podobnie jak `Actor`, każdy reżyser jest unikalną encją przechowywaną w grafie, co umożliwia łatwe odnajdywanie wszystkich dzieł danego twórcy.

**Category**: Węzeł ten reprezentuje gatunek lub kategorię, do której przypisany jest film (np. "Action", "Drama", "Sci-Fi"). Umożliwia to efektywne grupowanie i filtrowanie filmów na podstawie ich przynależności gatunkowej.

**Description**: Węzeł przechowujący tekstowy opis fabuły filmu (`text`). Jest on zawsze powiązany z węzłem `Language`, co pozwala na przechowywanie wielu wersji językowych opisów dla tego samego filmu.

**WikipediaArticle**: Węzeł zawierający pełną treść artykułu z Wikipedii (`text`) dotyczącego danego filmu. Podobnie jak `Description`, jest on połączony z węzłem `Language`, co umożliwia obsługę treści wielojęzycznych i dostarczanie użytkownikom bogatszego kontekstu.

**Language**: Węzeł techniczny, który przechowuje informacje o języku (kod oraz pełną nazwę, np. "en" i "English"). Jego celem jest kategoryzacja treści tekstowych (`Description`, `WikipediaArticle`), co umożliwia filtrowanie i dostarczanie danych w preferowanym przez użytkownika języku.

### 4.1.2. Relacje

Relacje definiują dynamiczne powiązania między węzłami, nadając im kontekst i znaczenie.

`(:Movie)-[:ACTED_IN]->(:Actor)`: Tworzy połączenie od węzła `Movie` do węzła `Actor`, wskazując, że dany aktor wystąpił w tym filmie. Umożliwia to nawigację od filmu do jego pełnej obsady.

`(:Director)-[:DIRECTED_BY]->(:Movie)`: Tworzy połączenie od węzła `Director` do węzła `Movie`. Kierunek tej relacji wskazuje, że reżyser jest twórcą filmu. Pozwala to na odnalezienie reżysera dla danego filmu.

`(:Movie)-[:HAS_CATEGORY]->(:Category)`: Przypisuje film do określonej kategorii lub gatunku, tworząc połączenie między węzłem `Movie` a `Category`.

`(:Movie)-[:HAS_DESCRIPTION]->(:Description)`: Łączy węzeł Movie z węzłem `Description`, który zawiera jego opis fabuły.

`(:Movie)-[:HAS_WIKI_ARTICLE]->(:WikipediaArticle)`: Łączy węzeł `Movie` z węzłem `WikipediaArticle`, udostępniając rozszerzone informacje na jego temat.

`(:Description)-[:IN_LANGUAGE]->(:Language)` oraz `(:WikipediaArticle)-[:IN_LANGUAGE]->(:Language)`: Te techniczne relacje łączą węzły z treścią tekstową z odpowiednim węzłem `Language`, precyzyjnie określając język danego tekstu.

## 4.2. Przetwarzanie Danych Wejściowych

Proces zasilania grafu wiedzy jest w pełni zautomatyzowany. Dane wejściowe pochodzą z zewnętrznej, relacyjnej bazy danych i są przetwarzane w wieloetapowym procesie w celu transformacji do modelu grafowego.

### 4.2.1. Pobieranie Danych

Pierwszy etap polega na pobraniu surowych danych z relacyjnej bazy danych za pomocą repozytoriów opartych na JPA (`Java Persistence API`). Aby uniknąć problemów z wydajnością związanych z pobieraniem dużej ilości danych tekstowych (`LOB - Large Object`) w jednym zapytaniu, dane te (opisy, artykuły) są pobierane w sposób jawny, w oddzielnych, dedykowanych zapytaniach dla każdego filmu.

### 4.2.2. Transformacja Danych

W tym kluczowym etapie płaska struktura relacyjna jest przekształcana w obiektowy model grafu. Serwis `GraphDataLoader` iteruje po pobranych encjach JPA i mapuje je na instancje klas Javy (`POJO`), które odpowiadają węzłom i relacjom zdefiniowanym w schemacie Neo4j. Jest to centralny punkt logiki biznesowej, gdzie dane są przygotowywane do zapisu w grafie.

### 4.2.3. Synchronizacja z Grafem

Ostatnim krokiem jest fizyczny zapis przygotowanej struktury w bazie danych Neo4j. Serwis `GraphSyncService` przekazuje listę przetworzonych obiektów do repozytorium Spring Data Neo4j, które automatycznie tłumaczy je na odpowiednie zapytania w języku Cypher i zapisuje węzły oraz relacje w grafie.

## 4.3. Metody Walidacji Danych i Zapewnienia Jakości

W celu zapewnienia wysokiej spójności, integralności i jakości danych w grafie wiedzy zaimplementowano szereg mechanizmów kontrolnych.

### 4.3.1. Obsługa Braku Danych

Podczas transformacji danych system rygorystycznie weryfikuje, czy obiekty, które mają być połączone relacją, nie są puste (`null`). Jeśli na przykład film w bazie źródłowej nie ma przypisanego reżysera, relacja `DIRECTED_BY` nie zostanie utworzona. Chroni to graf przed powstawaniem wiszących relacji, które prowadziłyby do błędów w zapytaniach i niespójności danych.

### 4.3.2. Deduplikacja Węzłów

Podczas przetwarzania danych wykorzystywany jest mechanizm buforowania (`cache`) w pamięci dla encji takich jak aktorzy, reżyserzy czy kategorie. Przed utworzeniem nowego węzła system sprawdza, czy encja o danym identyfikatorze nie została już przetworzona. Jeśli tak, ponownie wykorzystuje istniejący obiekt. Jest to kluczowe dla integralności grafu, ponieważ gwarantuje, że np. "Tom Hanks" jest jedną, unikalną encją w całym grafie, a nie wieloma węzłami tworzonymi dla każdego filmu z jego udziałem. Umożliwia to wiarygodną analizę sieci powiązań.

### 4.3.3. Integralność Transakcyjna

Cały proces synchronizacji danych z Neo4j jest operacją atomową, zarządzaną w ramach jednej transakcji. Oznacza to, że zapis tysięcy węzłów i relacji odbywa się na zasadzie *"wszystko albo nic"*. W przypadku wystąpienia jakiegokolwiek błędu (np. błędu sieci, naruszenia ograniczeń bazy danych), cała operacja jest automatycznie wycofywana. Chroni to graf przed pozostaniem w częściowo zaktualizowanym, niespójnym stanie.

### 4.3.4. Spójność Schematu Źródłowego

Pierwszą linią obrony jakości danych są ograniczenia integralności zdefiniowane w źródłowej, relacyjnej bazie danych. Reguły takie jak klucze obce, ograniczenia `NOT NULL` czy unikalne indeksy zapewniają, że dane trafiające do procesu transformacji grafu są już wstępnie zwalidowane i spójne.

# 5. Dostępne typy zapytań

W systemie Popcornium obsługiwanych jest sześć typów zapytań dotyczących filmów. Każdy z typów zapytań posiada inny prompt warunkujący zachowanie modelu i jego podejście do pytania zadanego przez użytkownika.

Poza tymi sześcioma zapytaniami dostępne jest również zapytanie generalne, służące do obsłużenia pytania użytkownika w sytuacji, kiedy system nie mógł poprawnie zidentyfikować typu zapytania, które ma być zrealizowane, bądź do przygotowania finalnej odpowiedzi na podstawie pozyskanych danych, tak jak w przypadku typu filtrowania. Poniżej zostały opisane wszystkie z dostępnych typów zapytań. 

## 5.1. Zliczanie

Celem tego typu zapytania jest uzyskanie wyniku liczbowego lub statystyk. Przykładowo, zapytanie użytkownika mające na celu uzyskanie informacji ile filmów nakręcił dany reżyser, bądź w ilu filmach zagrał dany aktor, powinno zostać obsłużone przez ten właśnie typ zapytania. Do implementacji tego typu zapytania wykorzystano poniższy prompt:

**Prompt zliczania**
```text
You are an AI assistant specialized in answering counting queries about movies.

Rules:
- Use only the provided context.
- Identify the filtering criteria from the question.
- Count entities explicitly present in the context.
- Do not estimate or hallucinate numbers.
- If the context is insufficient, say so.

{{question}}

Context:

{{context}}

Task:
Count how many entities satisfy the criteria.
Provide the final number and a short explanation.
```

## 5.2. Filtrowanie

Filtrowanie w systemie Popcornium służy do ekstrakcji parametrów zapytania, które następnie zostanie wykonane na bazie danych, z pytania zadanego przez użytkownika. Dzięki temu typowi zapytania, możliwe jest wyodrębnienie kryteriów zapytania, możliwego do wykonania w formie zapytania SQL, z pierwotnego pytania użytkownika zadanego w języku naturalnym. Cel ten jest realizowany za pomocą poniższego prompta:

**Prompt filtrowania**
```text
You are Popcornium, a movie database assistant.

Your task is to extract structured filtering criteria from a user's query about movies.

Rules:
- Do NOT answer the question directly.
- Do NOT describe movies.
- Only extract filters that can be applied to a movie database.
- If a value is not mentioned, omit it.
- If a filter is ambiguous, choose the most likely interpretation

Return the result as valid JSON only.

Extract movie filtering criteria from the following query:

Query:
{{query}}

Possible fields:
- title
- genre
- director
- actor
- yearFrom
- yearTo
- rating
- ratingCount

Return JSON only. Do not include explanations.
```
## 5.3. Agregacja

W Popcornium agregacja służy do generowania różnego rodzaju podsumowań, analizy trendów czy statystyk. Pozwala ona na grupowanie danych bądź wyliczanie wartości zbiorczych. W celu implementacji tego typu zapytania wykorzystano poniższy prompt:

**Prompt agregacji**
```text
You are Popcornium, a movie expert who specializes in statistics and trends.
Answer in Polish, clearly and concisely.
Focus on providing summaries, trends, counts, and aggregated insights based on the data provided.
Avoid spoilers.

{{query}}

Context:

{{context}}

Task:
Provide a clear summary, trends, or aggregated insights in response.
Answer in polish.
```

## 5.4. Wnioskowanie

Wnioskowanie w systemie Popcornium ma na celu obsługę wszelkiego rodzaju zapytań, gdzie kluczowe jest przeanalizowanie dostępynch danych, w celu zapewnienia logicznych wniosków opartych na tych danych. Odpowiedź do tego rodzaju zapytań zazwyczaj nie znajduje się bezpośrednio w danych, a model jest proszony o dostarczenie ich interpretacji w języku natrulanym. Wnioskowanie może relizować wszelkiego rodzaju zapytania o podobieństwo filmów, czy rekomendacji na podstawie preferencji użytkownika. Do realizacji wnioskowania został użyty poniższy prompt:

**Prompt wnioskowania**
```text
You are Popcornium, a movie expert capable of reasoning and making logical inferences.
Answer in Polish clearly and concisely.
Use available context and your knowledge to provide a reasoning-based answer.
Avoid spoilers unless explicitly requested.

{{query}}

Context (if any):

{{context}}

Task:
Use reasoning to provide a clear answer. Connect facts logically and explain you conclusion.
Answer in Polish.
```

## 5.5. Czasowe

Obsługa zapytań czasowych pozwala na analizę lub interpretację danych w kontekście dat i okresów. Dzięki temu typowi zapytania możliwa jest analiza relacji w czasie, analiza zmian czy kolejności. Do realizacji tego typu zapytania użyto poniższego prompta:

**Prompt zapytania czasowego**
```text
You are Popcornium, a movie expert specialized in timelines and chronological events.
Answer in Polish clearly and concisely.
Focus on time relations, order of events, and historical trends.
Avoid spoilers unless requested.

{{query}}

Context (if available):

{{context}}

Provide a clear answer respecting chronological order or temporal relationships.
Answer in Polish.
```
## 5.6. Złożone

W systemie Popcornium zapytanie złożone to takie zapytanie, które pozwala na realizację zapytań użytkowników, których poprawne obsłużenie wymaga wykonania kombinacji dowolnej ilości z wcześniej przedstawionych zapytań. Zapytanie złożone składa się z minimum dwóch z wcześniej omówionych typów zapytań. Szczegółowy mechanizm działania tego typu zapytania zostanie omówiony w następym rozdziale. W przeciwieństwie do poprzednich typów zapytań, zapytanie złożone nie posiada jednego przypisanego sobie prompta, a zamiast tego korzysta z wielu różnych promptów dostępnych w ramach systemu Popcornium.

## 5.7. Zapytanie generalne

Zapytanie generalne służy do obsługi zapytań we wszystkich sytuacjach, w których nie udało się poprawnie zidentyfikować intencji użytkownika. Służy również do budowania finalnych odopwiedzi w języku natrulanym po poprawnym wykonaniu się zapytania złożonego, jak również zapytania filtrującego. Do realizacji zapytania generalnego wykorzystano poniższy prompt: 

**Prompt zapytania generalnego**
```text
You are Popcornium, a movie expert with extensive knowledge of world cinema.

Rules:
- Answer in Polish, concisely and clearly.
- Avoid spoilers unless explicitly requested.
- You can't answer questions not related to movies.
```

# 6. Mechanizm obsługi zapytań

Mechanizm obsługi zapytań w aplikacji Popcornium został zaprojektowany jako wieloetapowy proces, którego celem jest poprawna interpretacja intencji użytkownika oraz wygenerowanie odpowiedzi adekwatnej do rodzaju zapytania. Architektura rozwiązania opiera się na separacji odpowiedzialności, wzorcach strategii oraz integracji z modelem językowym.

## 6.1. Ogólny przepływ przetwarzania zapytania

Obsługa zapytania użytkownika składa się z poniższych głównych etapów:

1. Otrzymanie zapytania w postaci tekstowej od użytkownika
2. Detekcja podstawowej intencji zapytania
3. W przypadku detekcji intencji złożonej - zaplanowanie wykonania zapytania
4. Budowa kontekstu dla modelu językowego
5. Wybór odpowiedniej strategii obsługi zapytania
6. Wygenerowanie odpowiedzi przez model językowy

<figure style="text-align: center;">
  <img src="documentation/seq_main_flow.png"
       alt="Diagram wzorca strategii"
       width="100%" />
  <figcaption>
    Rysunek 6.1: Diagram sekwencji przedstawiający przetworzenie zapytania
  </figcaption>
</figure>

## 6.2. Detekcja intencji zapytania

Pierwszym etapem przetwarzania zapytania użytkownika jest detekcja intencji. Odpowiada za to komponent **IntentionDetector**.

Proces detekcji intencji polega na:

- przygotowaniu odpowiedniego promptu systemowego
- wypełnieniu prompta zapytaniem użytkownika
- uzyskaniu, krótkiej i jednoznacznej odpowiedzi

Poniżej został przedstawiony prompt, który w systemie Popcornium jest wykorzysytwany do detekcji intencji użytkownika:

**Prompt detektora intencji**
```text
You are a query classifier.
Classify the user's query into exactly ONE of the following intentions:

- COUNTING: the answer is a number
- FILTERING: the result is a list of movies or entities
- AGGREGATION: summaries, statistics, trends
- REASONING: inference or explanation not explicitly stated in data
- TEMPORAL: time-based relationships, changes, or ordering
- COMPLEX: combination of multiple intentions
- GENERAL: general movie-related conversation

User query:

"{{question}}"

Respond with ONLY the intention name.
```

Wynikiem działania detektora intencji jest jedna z dostępnych wartości enumeracji
**Intention**:

- COUNTING
- FILTERING
- AGGREGATION
- REASONING
- TEMPORAL
- COMPLEX
- GENERAL

Intencje te odpowiadają typom zapytań, które zostały opisane w punkcie piątym dokumentacji. Mechanizm został skonstruowany tak, aby w przypadku braku możliwości jednoznaczego określenia jaką intencję przypisać danemu zapytaniu od użytkownika lub błędu parsowania, system domyślnie użył intencji generalnej. Podejście to zapewnia, że nawet jeśli wystąpi błąd, to zapytanie zostanie zawsze obsłużone.

## 6.3. Kontekst przetwarzania zapytania

Stan przetwarzania zapytania jest przechowywany w obiekcie **LlmContext**. Obiekt ten pełni rolę kontenera kontekstu logicznego pomiędzy kolejnymi etapami obsługi zapytania.

Kontekst przechowuje:
- wykrytą podstawową intencję zapytania
- zestaw atrybutów pośrednich (wyniki częściowe z poszczególnych kroków w planie zapytania)
- końcowy kontekst, który jest przekazywany do modelu językowego, podczas budowania finalnej odpowiedzi

<figure style="text-align: center;">
  <img src="documentation/cd_context.png"
       alt="Diagram wzorca strategii"
       width="1000%" />
  <figcaption>
    Rysunek 6.2: Diagram klas przedstawiający kontekst i  struktury pomocnicze
  </figcaption>
</figure>

Dla zapytań prostych, czyli tych których obsłużenie jest możliwe tylko za pomocą jednej z dostępnych intencji kontekst jest budowany jednorazowo, natomiast dla zapytań złożonych może być uzupełniany iteracyjnie.

## 6.4. Obsługa zapytań złożonych i planowanie wykonania

W przypadku wykrycia zapytania złożonego - intencja COMLPEX, system wykorzystuje komponent **ComplexExecutionPlanner**. Celem tego komponentu jest przekazanie pierwotnego zapytania od użytkownika do modelu językowego, którego zadaniem jest na podstawie określonych reguł, stworzenie planu wykonania w postaci strukturalnej (JSON), 
następnie komponent planera wykonania z otrzymanego obiektu stworzy gotowy plan złożony z listy kroków (ExecutionStep). Gotowy plan zostanie przekazany dalej do wykonania przez **LlmService**. Poniżej prompt, który jest wykorzysytwany w systemie Popcornium do tworzenia planów wykonania:

**Prompt planera zapytań**
```text
You are a deterministic execution planner.

Your task is to break a complex question into an ordered list of execution steps.

Rules:
- Each step must map to EXACTLY ONE intention
- Each step must declare:
    - what it produces (outputKey)
    - what previous outputs it depends on (dependsOn)
    - if it should do own rag retrieval or use other step knowledge or both
- Steps MUST NOT assume implicit knowledge
- If a step refers to "this", "that", or "the result", it MUST declare a dependency
- Queries must be scoped ONLY to the step responsibility
- Do NOT repeat the full user question
- Plan should contain maximum one step of each type, do not create too complex plans
- Return ONLY valid JSON

User question:
{{query}}

Available intentions:
- FILTERING: selecting entities or subsets
- COUNTING: producing numeric results
- AGGREGATION: statistics, trends, summaries
- TEMPORAL: ordering or change over time
- REASONING: inference based on previous results

Return a JSON array of steps in execution order.

Each step MUST follow this schema:

{
"intention": "FILTERING | COUNTING | AGGREGATION | TEMPORAL | REASONING",
"query": "precise task description for this step only",
"dependsOn": ["OUTPUT_KEY_1", "OUTPUT_KEY_2"],
"outputKey": "UNIQUE_RESULT_NAME", 
"allowRag": true | false
}


If no dependencies are required, use an empty array.
```

Parsowanie planu jest realizowane przez **ExecutionPlanParser**, który wydziela fragment
JSON z odpowiedzi modelu i mapuje go na strukturę danych.

<figure style="text-align: center;">
  <img src="documentation/seq_planner.png"
       alt="Diagram wzorca strategii"
       width="100%" />
  <figcaption>
    Rysunek 6.3: Diagram sekwencji przedstawiający proces planowania wykonania zapytania
  </figcaption>
</figure>

Po otrzymaniu gotowej struktury danych **LlmService** przechodzi przez poszczególne kroki zapytania i wykorzystując **LlmContextHandler** akumuluje wyniki cząstkowe w rejestrze kontekstu. Dzięk temu poszczególne kroki planu mogą wyciągać informacje z contextu lub do niego wkładać informacje, które same otrzymały z modelu. Po przejścu przez wszystkie kroki planu, budowany jest finalny kontekst który dołączany jest do finalnego zapytania do modelu, wykonywanego za pomocą generalnej intencji. Odpowiedź modelu po tym etapie jest zwracana użytkownikowi jako finalna odpowiedź. 

## 6.5. Mechanizm kontekstowy i obsługa zależności

Podczas obsługi zapytań złożonych, komponent **LlmContextHandler** odpowiada za:

- ustawienie aktualnie przetwarzanego kroku
- pobieranie wyników zależnych kroków
- warunkowe dołączanie kontekstu z mechanizmu RAG
- agregowanie wyników cząstkowych

<figure style="text-align: center;">
  <img src="documentation/seq_execution.png"
       alt="Diagram wzorca strategii"
       width="100%" />
  <figcaption>
    Rysunek 6.4: Diagram sekwencji pokazujący mechanizm kontekstowy i obsługę zależności
  </figcaption>
</figure>

Dzięki zastosowaniu tego mechanizmu każdy pojedyńczy krok planu wykonania ma możliwość korzystania z wyników poprzednich operacji, co umożliwia wieloetapowe wnioskowanie.

## 6.6. Wzorzec strategii obsługi zapytań

Właściwa obsługa zapytania realizowana jest przy użyciu wzorca **Strategy**.
Interfejs **QueryStrategy** definiuje wspólny kontrakt dla wszystkich strategii zapytań. Każda ze strategii:

- obsługuje dokładnie jeden typ intencji
- przygotowuje odpowiedni prompt systemowy
- buduje żądanie do modelu językowego

Rejestr strategii - **QueryStrategyRegistry**, umożliwia dynamiczny wybór odpowiedniej implementacji na podstawie wykrytej intencji. Dzięki zastosowaniu tego mechanizmu rozszerzanie systemu o nowe typy zapytań jest stosunkowo proste, ponieważ wymaga dostarczenia nowej implementacji wyżej wspomnianego interfejsu, oraz dodania informacji o nowej intencji do detektora intencji. Umożliwa to skalowalność i pozwala na łatwą rozbudowę systemu w przyszłych iteracjach.

<figure style="text-align: center;">
  <img src="documentation/cd_strategy.png"
       alt="Diagram wzorca strategii"
       width="100%" />
  <figcaption>
    Rysunek 6.5: Diagram klas przedstawiający wzorzec strategii obłsugi zapytań
  </figcaption>
</figure>

## 6.7. Integracja z modelem językowym

Przetwarzanie każdej ze strategii kończy się wygenerowaniem obiektu **ChatRequest**, który zawiera:

- prompt systemowy
- wiadomość użytkownika
- opcjonaly kontekst
- parametry generowania (temperatura, limit tokenów)
- metadane opisujące intencję

Odpowiedź modelu jest następnie zwracana użytkownikowi jako finalny rezultat zapytania.

## 6.8. Podsumowanie mechanizmu

Zastosowana architektura umożliwia łatwe dodawanie nowych typów zapytań i wspiera zapytania wieloetapowe, pozwala na separację logiki interpretacji od logiki wykonania oraz pozwala na elastyczną integrację z mechanizmami RAG.

<figure style="text-align: center;">
  <img src="documentation/dd_architecture.png"
       alt="Diagram wzorca strategii"
       width="100%" />
  <figcaption>
    Rysunek 6.6: Diagram komponentów obsługi zapytań
  </figcaption>
</figure>

# 7. Wyniki eksperymentów - Bartosz

# 8. Wnioski i rekomendacje - Bartosz

# 9. Instrukcja uruchomienia - Krzysztof


