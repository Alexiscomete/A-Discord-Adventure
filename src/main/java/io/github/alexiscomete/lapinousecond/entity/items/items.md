
# Comment concevoir les items ?

## Cahier des charges

- Les items doivent avoir un arbre des types (ex: `Item` -> `Weapon` -> `Sword` -> `Wooden Sword` -> `...`)
  - Chaque type ajoute des propriétés à l'item et des fonctionnalités, donc des fonctions
  - Réfléchir : évolution des items ?
- Les items doivent être stockés dans un inventaire
  - Inventaire de taille fixe
  - Inventaire de taille variable
  - Inventaire de taille fixe avec des emplacements spéciaux (ex : emplacement pour l'arme)
- Les items doivent pouvoir être améliorés
- Les items doivent pouvoir être équipés
- Les items peuvent être utilisés dans des recettes / crafts
- Les items doivent pouvoir être vendu / supprimés
- Les items doivent être sauvegardables dans le SQL
- Les items doivent être récupérables depuis le SQL

## Solutions possibles

### Solution 1 : héritage

- `Item` est une classe abstraite
- `Weapon` est une classe abstraite
- etc ...

C'est la solution la plus simple, mais à elle toute seule elle ne permet pas de faire évoluer les items sauf en créant un nouvel item à chaque fois.

### Solution 2 : composition

- `ItemSave` est une classe
- `Item` est une classe abstraite

Composition de `Item` :
- `ItemSave` : contient les données de l'item en SQL avec un pointeur vers l'item
- `Item` : contient les fonctions de l'item et un pointeur vers l'item en SQL avec en plus des données facilement accessibles

C'est la solution la plus compliquée, mais elle permet de faire évoluer les items. Cependant, il est difficile de récupérer la classe la plus extérieure d'un item et donc de faire des actions sur l'item.

### Solution 3 : SQL simple

- `Item` est une classe

Ne permets pas de faire évoluer les items facilement, mais permet de récupérer facilement la classe de l'item. Solution très simple, mais pas très élégante. Une solution pour faire évoluer les items serait de faire différents états et de "switcher" entre les eux. Le fichier risque de devenir lourd et pas simplement modifiable.
