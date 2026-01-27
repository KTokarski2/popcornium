# 🍿Popcornium - wirtualny doradca filmowy

<br>

**Krzysztof Tokarski**  
**Marek Karpiński**  
**Bartosz Warda**

<br>

Warszawa  
Styczeń 2026

# 1. Wprowadzenie - Marek

## 1.1 Podtytuł

### 1.1.1 Podtytuł

[example sequnece diagram](documentation/example-diagram.png)

# 2. Opis architektury systemu - Marek

# 3. Embeddings - Marek

# 4. Graf wiedzy - Bartosz

System wykorzystuje grafową bazę danych Neo4j w celu stworzenia zaawansowanego Grafu Wiedzy. Model ten pozwala na efektywne reprezentowanie, przechowywanie i odpytywanie złożonych, wielowymiarowych relacji między danymi dotyczącymi filmów, ich twórców oraz powiązanych treści. Graf wiedzy jest budowany w sposób dynamiczny na podstawie danych pochodzących z zewnętrznego, relacyjnego źródła danych, co zapewnia jego aktualność i spójność.

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

# 5. Dostępne typy zapytań - Krzysztof 

# 6. Mechanizm obsługi zapytań - Krzysztof

# 7. Wyniki eksperymentów - Bartosz

# 8. Wnioski i rekomendacje - Bartosz

# 9. Instrukcja uruchomienia - Krzysztof


