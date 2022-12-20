# Data Object
> A simple tool to store Data Object

## API Usage

### Get an object (download file)
> GET /v1/objects/{objectName}

### Create an object
> POST /v1/objects/{objectName}

#### Request body as Content-Type: multipart

```
{
  "file": "file"
}
```

### Update an object
> PATCH /v1/objects/{objectName}

### Delete an object
> DELETE /v1/objects/{objectName}

### Get publish link
> GET /v1/objects/{objectName}/link

### Create root object
> GET /v1/root-objects/{objectName}

### Delete root object
> DELETE /v1/root-objects/{objectName}
