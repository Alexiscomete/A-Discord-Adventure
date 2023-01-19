
# Comment concevoir les items ?

## Cahier des charges

- Les items doivent avoir un arbre des types (ex: `Item` -> `Weapon` -> `Sword` -> `Wooden Sword` -> `...`)
  - Chaque type ajoute des propriétés à l'item et des fonctionnalités, donc des fonctions
  - Pas besoin de faire évoluer les items : les crafts et les améliorations suffisent
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

C'est la solution la plus simple, mais elle ne permet pas de faire de l'héritage multiple (ce qu'il est possible de faire avec des interfaces).

### Solution 2 : composition

- `ItemSave` est une classe
- `Item` est une classe abstraite

Composition de `Item` :
- `ItemSave` : contient les données de l'item en SQL avec un pointeur vers l'item
- `Item` : contient les fonctions de l'item et un pointeur vers l'item en SQL avec en plus des données facilement accessibles

C'est la solution la plus compliquée, mais elle permet de faire évoluer les items. Cependant, il est difficile de récupérer la classe la plus extérieure d'un item et donc de faire des actions celui-ci.

### Solution 2 bis : composition inversée

Item devient la classe principale qui interagit avec ItemSave. Il est aussi possible de ne pas faire item save et de faire en sorte que l'item soit indépendant de sa sauvegarde. Cela rend par contre la sauvegarde plus difficile.

### Solution 3 : mix de tout ça

- `Item` la classe abstraite principale SQL
- Les autres classes sont implémentées à partir de `Item`
- Des interfaces sont créées de façon indépendante pour les fonctions

Je vais prendre cette solution.
