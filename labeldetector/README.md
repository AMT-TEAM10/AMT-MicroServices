# Label Detector

## Utilisation de l'API

### Labellisation d'une image

> POST /v1/labels

#### Requête Content-Type: application/json

```json
{
  "imageUrl": "https://upload.wikimedia.org/wikipedia/commons/9/9d/NYC_Montage_2014_4_-_Jleon.jpg",
  "maxLabels": 10,
  "minConfidence": 0.5
}
```

#### Contenu de la réponse Content-Type: application/json

La réponse contient :
- count: nombre de labels retournés
- sourceUrl: url de l'image analysée
- results: tableau de labels identifiés dans l'image

```json
{
  "count": 10,
  "sourceUrl": "https://upload.wikimedia.org/wikipedia/commons/9/9d/NYC_Montage_2014_4_-_Jleon.jpg",
  "results": [
    {
      "name": "City",
      "confidence": 1.0
    },
    {
      "name": "Metropolis",
      "confidence": 1.0
    },
    {
      "name": "Urban",
      "confidence": 1.0
    },
    {
      "name": "Art",
      "confidence": 0.9997783
    },
    {
      "name": "Collage",
      "confidence": 0.9997059
    },
    {
      "name": "High Rise",
      "confidence": 0.99822485
    },
    {
      "name": "Building",
      "confidence": 0.99822485
    },
    {
      "name": "Office Building",
      "confidence": 0.99667853
    },
    {
      "name": "Person",
      "confidence": 0.960994
    },
    {
      "name": "Arch",
      "confidence": 0.71438813
    }
  ]
}
```

#### CURL exemple

Exemple curl

```bash
curl --location --request POST 'http://localhost:8080/v1/labels' \
--header 'Content-Type: application/json' \
--data-raw '{
  "imageUrl": "https://upload.wikimedia.org/wikipedia/commons/9/9d/NYC_Montage_2014_4_-_Jleon.jpg",
  "maxLabels": 10,
  "minConfidence": 0.5
}'
```
