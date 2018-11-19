#Utilisation

## Compilation

Lancer dans le dossier "src" du projet :
```sh
javac *.java
```
## Décryptage d'un fichier (Vigenere)
```sh
java Main -decrypt-vig [filename] -key [key]
```
## Cryptage d'un fichier (Vigenere)
```sh
java Main -encrypt-vig [filename] -key [key]
```
## Crack fichier (Vigenere)
```sh
java Main -crack-vig [filename]
```

## Cryptage charactere (SDES)
```sh
java Main -sdes [character]
```


## Cryptage/Decryptage RSA
Pour l'instant il n'est pas disponible dans le main.
Il faut modifier à la main la variable "message" dans le main de la classe RSA et lancer:
̀``sh
java Rsa
```