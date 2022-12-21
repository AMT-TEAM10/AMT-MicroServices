# Label Detector

> A simple tool to detect labels in images

## API Usage

### Process an image

> POST /v1/labels

#### Request body as Content-Type: application/json

```json
{
  "imageUrl": "https://www.example.com/image.jpg",
  "maxLabels": 10,
  "minConfidence": 0.5
}
```

#### Response body as Content-Type: application/json

The response is an array of Labels:

```json
[
  {
    "name": "label name",
    "confidence": 0.9
  }
]
```

#### CURL example

Here is an example of a CURL request:

```bash
curl --location --request POST 'localhost:8080/process' \
--header 'Content-Type: application/json' \
--data-raw '{
    "imageUrl": "https://www.rts.ch/2018/07/15/11/28/9715654.image",
    "maxLabels": 500,
    "minConfidence": 0.5
}'
```
