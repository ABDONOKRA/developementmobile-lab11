# Rapport de TP : Développement d'une Application de Géolocalisation avec Google Maps

## 1. Objectifs du Projet
Ce TP avait pour but de concevoir une application Android interactive intégrant les services Google Play. Les fonctionnalités principales sont :
- L'affichage d'une carte **Google Maps**.
- La gestion des **permissions de localisation** (Runtime Permissions).
- Le suivi en temps réel de la position de l'utilisateur via **GPS** et **Réseau**.
- La mise à jour dynamique d'un **marqueur** sur la carte.
- La gestion des états système (alerte si le GPS est désactivé).

---

## 2. Configuration Technique

### 2.1 Dépendances (`build.gradle.kts`)
Pour permettre l'utilisation des services Google, les bibliothèques suivantes ont été ajoutées :
- `com.google.android.gms:play-services-maps:19.0.0`
- `com.google.android.gms:play-services-location:21.3.0`

### 2.2 Permissions et Sécurité (`AndroidManifest.xml`)
L'accès aux capteurs nécessite des permissions explicites :
- `ACCESS_FINE_LOCATION` : Pour la position précise (GPS).
- `ACCESS_COARSE_LOCATION` : Pour la position approximative (Wi-Fi/Antennes).
- `INTERNET` & `ACCESS_NETWORK_STATE` : Pour le chargement des tuiles de la carte.

Nous avons également configuré la balise `<meta-data>` pour inclure la **clé API Google Cloud** nécessaire à l'authentification du service Maps.

---

## 3. Développement de l'Interface (`activity_main.xml`)
L'interface utilisateur repose sur un `SupportMapFragment`. C'est un conteneur optimisé qui gère automatiquement le cycle de vie de la carte Google.

```xml
<fragment 
    android:id="@+id/map_view"
    android:name="com.google.android.gms.maps.SupportMapFragment"
    android:layout_width="match_parent"
    android:layout_height="match_parent" />
```

---

## 4. Implémentation Logicielle (`MainActivity.java`)

### 4.1 Moteur de Rendu et Initialisation
Pour assurer une compatibilité maximale sur les émulateurs, nous avons forcé l'utilisation du moteur de rendu `LEGACY` via `MapsInitializer`. L'interface `OnMapReadyCallback` est implémentée pour configurer la carte dès qu'elle est disponible.

### 4.2 Écoute de la Position
Nous utilisons `LocationManager` pour recevoir des mises à jour régulières. 
- **Fréquence** : 2000ms (2 secondes).
- **Distance minimale** : 5 mètres.
- **Providers** : Utilisation combinée de `GPS_PROVIDER` et `NETWORK_PROVIDER`.

### 4.3 Logique de Mise à Jour
À chaque changement de position, l'application effectue :
1. La création ou le déplacement d'un **unique marqueur** (`Marker.setPosition()`) pour éviter de surcharger la carte.
2. Une **animation fluide** de la caméra vers les nouvelles coordonnées.
3. Un zoom automatique au niveau **15.0f** (niveau rue/quartier).

---

## 5. Gestion des Erreurs et Cas Particuliers

### 5.1 Alerte GPS
Si l'application détecte que la localisation est désactivée, une boîte de dialogue personnalisée (`AlertDialog`) s'affiche. Elle redirige l'utilisateur directement vers les **paramètres Android** via un `Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)`.

### 5.2 Résolution des problèmes de connexion
Lors des tests sur émulateur, des problèmes d'écran blanc ont été rencontrés. Ils ont été résolus en :
- Effectuant un **Cold Boot** de l'appareil virtuel.
- Paramétrant des **DNS Google (8.8.8.8)**.
- Activant le trafic HTTP via `usesCleartextTraffic="true"`.

<img width="822" height="1048" alt="image" src="https://github.com/user-attachments/assets/9f7a4e5e-06b5-46eb-8fc6-4dbe5b04bc7d" />

---

## 6. Conclusion
Ce projet a permis de maîtriser l'intégration d'API tierces complexes dans un environnement mobile. L'application finale est fluide, gère correctement les droits d'accès de l'utilisateur et offre une expérience de navigation intuitive grâce aux animations de caméra de Google Maps.
