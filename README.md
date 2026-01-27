# Popcornium - wirtualny doradca filmowy

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

# 6. Mechanizm obsługi zapytań - Krzysztof

# 7. Wyniki eksperymentów - Bartosz

# 8. Wnioski i rekomendacje - Bartosz

# 9. Instrukcja uruchomienia - Krzysztof


