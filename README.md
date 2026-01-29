# Popcornium - wirtualny doradca filmowy

<br>

**Krzysztof Tokarski**  
**Marek Karpiński**  
**Bartosz Warda**

<br>

Warszawa  
Styczeń 2026

# 1. Wprowadzenie
## 1.1 Opis problemu i motywacja
Współczesne aplikacje filmowe oferują dostęp do bardzo dużych zbiorów danych, jednak interakcja z nimi najczęściej ogranicza się do statycznego przeglądania list, filtrowania wyników lub prostego wyszukiwania po nazwach. W praktyce użytkownik, który chce uzyskać odpowiedź na bardziej złożone pytanie. Na przykład dotyczące powiązań pomiędzy filmami, aktorami i reżyserami lub rekomendacji opartych na własnych preferencjach, zmuszony jest do samodzielnego analizowania wielu źródeł informacji.

Motywacją do stworzenia systemu Popcornium było zaprojektowanie aplikacji, która umożliwia eksplorację bazy filmowej w sposób bardziej naturalny i intuicyjny. Kluczowym założeniem projektu było wykorzystanie interfejsu konwersacyjnego, pozwalającego na zadawanie pytań w języku naturalnym oraz uzyskiwanie odpowiedzi opartych na rzeczywistych danych przechowywanych w systemie. Projekt stanowi również praktyczne porównanie dwóch podejść do wyszukiwania wiedzy w systemach RAG: wyszukiwania wektorowego oraz wyszukiwania grafowego (GraphRAG).
## 1.2 Wizja systemu Popcornium

Popcornium to aplikacja webowa łącząca funkcjonalności klasycznej aplikacji filmowej z inteligentnym chatbotem opartym o modele językowe. System umożliwia użytkownikowi przeglądanie bazy filmów wraz z grafikami, dostęp do szczegółowych informacji o wybranych produkcjach, takich jak obsada, reżyser czy opis fabuły, a także tworzenie własnych watchlist i ocenianie filmów za pomocą prostych reakcji (łapka w górę / łapka w dół).

Centralnym elementem aplikacji jest chatbot kontekstowy, który wykorzystuje techniki RAG oraz GraphRAG. Chatbot uwzględnia zarówno dane domenowe przechowywane w systemie, jak i kontekst użytkownika obejmujący historię rozmów, wystawione oceny oraz elementy watchlist. Dzięki temu możliwe jest generowanie odpowiedzi bardziej trafnych, spójnych oraz lepiej dopasowanych do preferencji konkretnego użytkownika.
## 1.3 Zastosowane technologie

Backend aplikacji został zrealizowany w języku Java 21 z wykorzystaniem frameworka Spring Boot w wersji 3.5.8, co zapewnia wysoką stabilność, modularność oraz łatwą integrację z zewnętrznymi usługami. Do obsługi bezpieczeństwa zastosowano Spring Security w konfiguracji stateless z mechanizmem JWT, co pozwala na bezpieczną identyfikację użytkowników bez konieczności utrzymywania sesji po stronie serwera.

Dane aplikacyjne przechowywane są w relacyjnej bazie PostgreSQL, rozszerzonej o wtyczkę PgVector, która umożliwia efektywne przechowywanie oraz porównywanie embeddingów wykorzystywanych w wyszukiwaniu wektorowym. Relacje pomiędzy filmami, aktorami i reżyserami odwzorowane są dodatkowo w bazie grafowej Neo4j, wykorzystywanej w mechanizmie GraphRAG.

Warstwa frontendowa została zaimplementowana w React.js z użyciem biblioteki Material UI, co umożliwia budowę nowoczesnego i responsywnego interfejsu użytkownika.

System integruje się z zewnętrznymi źródłami danych, takimi jak IMDb oraz Wikipedia, które dostarczają informacji filmowych i opisowych. Generowanie embeddingów oraz odpowiedzi modelu językowego realizowane jest przy użyciu usługi Azure OpenAI. Pliki multimedialne, w szczególności zdjęcia filmów i aktorów, przechowywane są w obiektowym systemie MinIO. Całość infrastruktury została skonteneryzowana z użyciem Docker oraz Docker Compose.
### Backend

- Java 21

- Spring Boot 3.5.8

- Spring Security (stateless, JWT)

- PostgreSQL + PgVector

- Neo4j

### Frontend

- React.js
- MaterialUI

### Integracje zewnętrzne

- IMDb – źródło danych filmowych

- Wikipedia – dodatkowe informacje opisowe

- Azure OpenAI – generowanie embeddingów oraz odpowiedzi LLM

- MinIO – przechowywanie zdjęć filmów i aktorów

### Infrastruktura

- Docker / Docker Compose - pełna konteneryzacja aplikacji i usług

# 2. Opis architektury systemu

Architektura systemu Popcornium została zaprojektowana w sposób modułowy, z wyraźnym podziałem odpowiedzialności pomiędzy komponentami odpowiedzialnymi za dane, wyszukiwanie wiedzy oraz interakcję z użytkownikiem.

## 2.1 Ogólny schemat architektury
<figure style="text-align: center;">
  <img src="documentation/diagram_2_1.png"
       alt="Diagram wzorca strategii"
       width="100%" />
  <figcaption>
    Rysunek 2.1 schemat przepływu danych
  </figcaption>
</figure>

        

## 2.2 Źródła danych
System korzysta zarówno z danych zewnętrznych, jak i danych generowanych przez użytkowników. Informacje filmowe pozyskiwane są z serwisów IMDb oraz Wikipedia, natomiast obrazy filmów i aktorów przechowywane są w systemie MinIO. Dane użytkowników obejmują tworzone watchlisty, wystawiane oceny oraz historię konwersacji z chatbotem i są zapisywane w relacyjnej bazie danych.

## 2.3 Moduły systemu
### 2.3.1 Moduł integracji

Moduł odpowiedzialny za pobieranie i inicjalne ładowanie danych filmowych do relacyjnej bazy danych. Dane są mapowane na encje domenowe oraz zapisywane przy starcie aplikacji.

### 2.3.2 Warstwa danych

Relacyjna baza danych PostgreSQL pełni rolę głównego repozytorium danych aplikacyjnych. Przechowywane są w niej informacje filmowe prezentowane w interfejsie użytkownika, embeddingi wykorzystywane w wyszukiwaniu wektorowym oraz dane użytkowników, takie jak oceny i watchlisty. Uzupełnieniem tej warstwy jest baza grafowa Neo4j, która przechowuje relacje pomiędzy encjami domenowymi i umożliwia realizację zapytań grafowych w ramach GraphRAG.

### 2.3.3 Chatbot i warstwa RAG

Chatbot stanowi centralny komponent systemu. Odpowiada on za analizę intencji zapytania użytkownika, wybór odpowiedniej strategii wyszukiwania (Vector RAG lub GraphRAG), wzbogacanie promptu o kontekst użytkownika oraz komunikację z usługą Azure OpenAI w celu wygenerowania odpowiedzi.

### 2.3.4 Moduł bezpieczeństwa

Moduł bezpieczeństwa oparty o Spring Security zapewnia uwierzytelnianie i autoryzację użytkowników z wykorzystaniem tokenów JWT. Rozwiązanie to umożliwia bezpieczny dostęp do zasobów API oraz jednoznaczną identyfikację użytkownika w operacjach takich jak ocenianie filmów, zarządzanie watchlistami czy personalizacja odpowiedzi chatbota.

### 2.3.5 Frontend Layer

Warstwa frontendowa, zaimplementowana w React.js, umożliwia użytkownikom interakcję z systemem poprzez przeglądanie filmów, korzystanie z chatbota oraz zarządzanie własnymi danymi. Frontend komunikuje się z backendem wyłącznie poprzez zabezpieczone API.

# 3. Embeddings

System Popcornium wykorzystuje embeddingi jako kluczowy mechanizm reprezentacji wiedzy tekstowej w ramach podejścia Retrieval-Augmented Generation (RAG). Celem zastosowania embeddingów jest umożliwienie semantycznego wyszukiwania informacji o filmach, aktorach oraz powiązanych kontekstach w sposób niezależny od dokładnej treści zapytania użytkownika.

Embeddingi stanowią warstwę pośrednią pomiędzy klasyczną bazą danych a modelem językowym, umożliwiając efektywne dopasowanie zapytań użytkownika do najbardziej relewantnych fragmentów wiedzy.

## 3.1 Model danych

Proces generowania embeddingów opiera się na danych przechowywanych w relacyjnej bazie danych, która pełni rolę głównego repozytorium wiedzy o filmach. Centralną encją w tym modelu jest obiekt Movie, który agreguje informacje o filmie, takie jak metadane produkcyjne, obsada, opisy fabularne oraz powiązane artykuły z Wikipedii.

Dane te są reprezentowane przez zestaw encji domenowych, obejmujących m.in. aktorów, reżyserów, kategorie filmowe, opisy fabuły oraz artykuły encyklopedyczne. Dodatkowo system przechowuje informacje o interakcjach użytkownika z filmami, takie jak oceny czy elementy watchlisty. Wygenerowane embeddingi zapisywane są w dedykowanej encji Embedding, która przechowuje zarówno wektor osadzenia, jak i treść źródłową, na podstawie której został on wygenerowany.

Tak zaprojektowany model danych umożliwia elastyczne przetwarzanie informacji o filmach oraz łatwe rozszerzanie systemu o kolejne źródła wiedzy.


## 3.2 Proces generowania embeddingów

Proces generowania embeddingów realizowany jest przez dedykowany komponent EmbeddingService, odpowiedzialny za przekształcanie danych tekstowych w reprezentację wektorową. Proces ten wykonywany jest cyklicznie lub inicjalnie, po załadowaniu danych filmowych do bazy.

Na początku system pobiera wszystkie filmy wraz z pełnym zestawem powiązanych danych. Następnie informacje o każdym filmie są dzielone na logiczne fragmenty, które odpowiadają różnym aspektom wiedzy o filmie. Taki podział danych (chunking) pozwala na zachowanie spójności semantycznej poszczególnych fragmentów oraz zwiększa precyzję późniejszego wyszukiwania.

Fragmenty te obejmują m.in. podstawowe metadane filmu, informacje o obsadzie, opis fabuły oraz treści pochodzące z Wikipedii. Każdy z tych fragmentów przetwarzany jest niezależnie i przekazywany do integracji z usługą Azure OpenAI, gdzie generowany jest embedding przy użyciu dedykowanego modelu do osadzeń tekstowych.

Wygenerowane wektory są następnie walidowane pod kątem poprawności rozmiaru i zapisywane w bazie danych wraz z informacją o typie fragmentu oraz jego oryginalnej treści. Dzięki temu system zachowuje pełną kontrolę nad tym, jaki kontekst został użyty podczas późniejszego wyszukiwania.

## 3.3 Wykorzystanie embeddingów w wyszukiwaniu semantycznym

Embeddingi zapisane w bazie danych wykorzystywane są podczas obsługi zapytań użytkownika w ramach strategii Vector RAG. W momencie zadania pytania system generuje embedding dla zapytania użytkownika, a następnie porównuje go z embeddingami zapisanymi w bazie danych.

Na podstawie podobieństwa semantycznego wybierane są najbardziej relewantne fragmenty wiedzy, które następnie przekazywane są do modelu językowego jako kontekst wspomagający generowanie odpowiedzi. Takie podejście pozwala na uzyskanie odpowiedzi opartych na faktycznych danych znajdujących się w systemie, a nie wyłącznie na wiedzy ogólnej modelu językowego.

## 3.4 Wykorzystanie kontekstu użytkownika

Oprócz embeddingów system Popcornium uwzględnia również kontekst związany z aktywnością użytkownika. Informacje takie jak oceny filmów, tworzone watchlisty czy historia rozmów z czatem wykorzystywane są podczas budowania zapytania przekazywanego do modelu językowego.

Kontekst użytkownika pozwala na personalizację odpowiedzi oraz lepsze dopasowanie rekomendacji do preferencji konkretnej osoby. Dane te nie są bezpośrednio przekształcane w embeddingi, lecz pełnią rolę dodatkowego kontekstu logicznego, wzbogacającego finalny prompt.

# 4. Graf wiedzy - Bartosz

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
       width="80%" />
  <figcaption>
    Rysunek 6.6: Diagram komponentów obsługi zapytań
  </figcaption>
</figure>

# 7. Wyniki eksperymentów - Bartosz

# 8. Wnioski i rekomendacje - Bartosz

# 9. Instrukcja uruchomienia - Krzysztof


