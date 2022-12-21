# Data Object

## Utilisation de l'API

### Récupérer un fichier (téléchargement)

> GET /v1/objects/{objectName}

### Exemple CURL

```bash
curl --location --request GET 'http://localhost:8080/v1/objects/main.jpeg'
```

### Créer un objet

> POST /v1/objects/{objectName}

#### Requête Content-Type: multipart/form-data

```bash
curl --location --request POST 'http://localhost:8080/v1/objects/test.png' \
--form 'file=@"/test.pdf"'
```

#### Réponse

```json
{
    "success": true,
    "message": "Object created"
}
```

### Update an object

> PATCH /v1/objects/{objectName}

#### Requête Content-Type: multipart/form-data

```bash
curl --location --request PATCH 'http://localhost:8080/v1/objects/test.png' \
--form 
```

#### Réponse

```json
{
    "success": true,
    "message": "Object created"
}
```

### Delete an object

> DELETE /v1/objects/{objectName}

#### Exemple CURL

```bash
curl --location --request DELETE 'http://localhost:8080/v1/objects/test.png'
```

#### Réponse

```json
{
    "success": true,
    "message": "Object deleted"
}
```

### Get publish link

> GET /v1/objects/{objectName}/link

#### Exemple CURL

```bash
curl --location --request GET 'http://localhost:8080/v1/objects/main.jpeg/link' \
--header 'Content-Type: application/json' \
--data-raw '{
    "imageUrl": "https://upload.wikimedia.org/wikipedia/commons/9/9d/NYC_Montage_2014_4_-_Jleon.jpg",
    "maxLabels": 5,
    "minConfidence": 0.99
}'
```

#### Réponse

```json
{
  "url": "https://...",
  "expirationDate": "21-12-2022 08:48:10"
}
```

### Create root object

> GET /v1/root-objects/{objectName}

#### Exemple CURL

```bash
curl --location --request POST 'http://localhost:8080/v1/root-objects'
```

#### Réponse

```json
{
  "success": true,
  "message": "Root object created"
}
```

ou

```json
{
  "success": false,
  "message": "Root object already exists"
}
```

