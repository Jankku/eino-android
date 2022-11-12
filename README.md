<h1 align="center">
<br>
  <img src="./screenshots/Eino.png" style="width: 80px;" />
<br>
Eino
</h1>
<div align="center">
<h4>Book and movie tracker with web and Android clients.</h4>
<h4>You can find backend code <a href="https://github.com/jankku/eino-backend/">here</a> and web frontend
code <a href="https://github.com/jankku/eino-web/">here</a>.</h4>
</div>

## Download

Get the app from [releases page](https://github.com/Jankku/eino-android/releases).

## Building

Create `baseurl.properties` file to the root folder with the following content:

```properties
baseUrl="<backend url>"
```

Now you can a debug build. If you want a release build, do the following:

Create `signing.properties` file to the root folder with the following content:

```properties
storeFile=<path to keystore>
keyAlias=
storePassword=
keyPassword=
```

Now you can build a release build.

## Screenshots

### Authentication
![Authentication](./screenshots/Authentication.png "Authentication")

### Books
![Books](screenshots/Books.png "Books")

### Movies
![Movies](screenshots/Movies.png "Movies")

### Search

![Search](screenshots/Search.png "Search")

### Profile

![Profile](screenshots/Profile.png "Profile")

### Settings

![Settings](screenshots/Settings.png "Settings")

## License

Licensed under the [MIT License](https://github.com/Jankku/eino-android/blob/master/LICENSE.md).
