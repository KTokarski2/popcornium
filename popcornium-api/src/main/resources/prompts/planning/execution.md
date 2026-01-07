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

```json
{
"intention": "FILTERING | COUNTING | AGGREGATION | TEMPORAL | REASONING",
"query": "precise task description for this step only",
"dependsOn": ["OUTPUT_KEY_1", "OUTPUT_KEY_2"],
"outputKey": "UNIQUE_RESULT_NAME"
}
```

If no dependencies are required, use an empty array.