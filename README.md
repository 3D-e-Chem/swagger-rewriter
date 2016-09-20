Use this script to rewrite a Swagger document to match the endpoints on a server.

Used to correct Swagger docs generated by Django (django-rest-swagger Python package) so it can be used to generate a Java client using the swagger code generator.

# Install

Download latest release from GitHub and unpack.

# Usage

```
swagger-rewrite/bin/swagger-rewrite <input swagger location> <rewrites.yaml> <output swagger filename>
```

# Supported rewrites

## Response 2 array

When response is defined in Swagger as type object, but endpoint returns array of type object.
 
Rewrites is yaml file using

```
responses2array:
- /services/proteinfamily/
```

For example will change
```
"type": "ProteinFamilySerializer"
```
into
```
"type": "array", 
"items": {
    "type": "ProteinFamilySerializer"
}
```

# Publish release

1. Bump version in `build.gradle` file
1. Commit & push
1. Create a release on GitHub. 
1. Run `./gradlew distZip`
1. Upload `build/distributions/*.zip` to GitHub release page.