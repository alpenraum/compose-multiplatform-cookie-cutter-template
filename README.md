# compose-multiplatform-cookie-cutter-template

A [Copier](https://copier.readthedocs.io) template for Compose Multiplatform apps targeting
Android and iOS, with optional desktop.

## Why use this template

### One error model from the data source to the screen

- Each data source changes its library exceptions into one sealed type, `AppError`. HTTP calls use
  `apiCall`. Database calls use `storageCall`.
- `BaseViewModel` is the only catch site for commands. `catchAsState` is the only catch site for data
  streams.
- `ErrorLocalizer` is the only place that makes error text. It uses an exhaustive `when`. If you add
  an error type, the code does not compile until the error has a text.
- Thus, a new data source needs only its own boundary function. The ViewModel, the error view and the
  snackbar operate without changes.
- The support detail shows only the error type and a path template. It does not show tokens, IDs or
  stack traces.

### An HTTP client with safe defaults

- The client sends GET, HEAD and OPTIONS requests again after a temporary failure. It does not send
  other requests again. Thus, a POST request does not occur two times.
- Each attempt has its own timeout. The client does not send a request again after a timeout.
- The error type gives the correct cause: no connection, a slow server, a server error, or a response
  that the app cannot read. The login page of a public Wi-Fi shows as "no connection", not as an app
  error.
- Twelve tests keep this behaviour. A change to the retry rules causes a test failure.

### Screens with one shape

- Each screen has one contract with a sealed state, intents and events. The state cannot show a
  combination of values that is not possible.
- Events go through a buffered channel. An event is not lost when the screen rotates.
- The ViewModel receives a `Navigator` with each intent. It does not keep a `NavController`. Thus,
  ViewModel tests run in `commonTest` without Android.
- The sample feature has a list screen, a detail screen and tests. You can copy the folder as the start
  of a new feature.

### A multiplatform build that operates

- Koin, KSP and Room are set up for all targets. A comment in the build file tells why each setting is
  necessary.
- Platform differences are `expect` declarations. If you add a target, the compiler shows each part
  that is missing.
- Release builds are set up: R8 for Android and ProGuard rules for desktop. Each rule tells which
  library needs it.
- Room exports the database schema. Room deletes the tables only on a downgrade. If you change the
  schema without a migration, the app fails at the first start during development. It does not delete
  the data of your users.

### Errors found before the app starts

- The template checks the names that you enter. It stops a name that cannot compile, for example a
  package with a Kotlin keyword.
- Two tests check the dependency graph. A missing Koin binding causes a test failure, not a crash on
  the first screen.
- Dependabot updates Compose with its AndroidX libraries, and Kotlin with KSP, in one group. These
  versions must change together.

### A template that you can test and update

- CI makes six combinations of options. It builds each combination for Android and desktop. It builds
  two combinations for iOS, with the Xcode project.
- CI also tests `copier update` from the previous template version.
- `copier update` gives your project the fixes that the template receives later. The files that you
  own stay as they are: `README.md`, `CLAUDE.md`, `docs/conventions.md` and `version.properties`.
- `docs/conventions.md` gives the rules that the code obeys. `CLAUDE.md` gives AI coding tools the
  commands and the rules.

## Usage

```bash
pipx install copier   # >= 9.19.1

# generate a new project
copier copy gh:alpenraum/compose-multiplatform-cookie-cutter-template my-app

# later: pull template changes into the generated project
cd my-app && copier update
```

`copier update` needs the generated project to be a git repository with a clean working tree.
`com.example.<app>` is only a placeholder — app stores reject it, so choose your own `package_name`.

Optional parts (persistence, networking, …) are chosen at copy time. Re-answering one with
`copier update` regenerates or deletes its files wholesale — switching an option off deletes your
edits to those files too, so treat that as a manual removal instead. Switching one on adds its code,
but `README.md`, `CLAUDE.md` and `docs/conventions.md` are not updated.
`README.md`, `CLAUDE.md`, `docs/conventions.md` and `version.properties` belong to the generated
project after the first copy; updates never overwrite them.

The template is untagged for now, so Copier uses the latest commit on `main`. Once tags exist,
Copier resolves to the newest tag instead.

## Options

| Question | Default | Generates |
|---|---|---|
| `app_name` | `MyApp` | PascalCase identifier for class names and the iOS product |
| `app_display_name` | `app_name` | Launcher / home-screen name (spaces allowed) |
| `package_name` | `com.example.<app>` | Package, Android application id, iOS bundle id |
| `author` | — | Desktop package vendor (asked only with desktop) |
| `include_persistence` | yes | Room (KMP, bundled SQLite, exported schemas), Room-backed sample repository, `AppError.Storage` |
| `include_networking` | yes | Ktor client (OkHttp/Darwin), `apiCall` HTTP boundary with typed `AppError.Network`, BuildKonfig base URLs, example remote data source |
| `include_bottom_nav` | yes | Adaptive navigation (bar on phones, rail on wide screens) with an Items and a Settings tab |
| `include_desktop` | no | JVM desktop target, packaging (Dmg/Msi/Deb) with ProGuard rules |
| `include_library_module` | no | An empty KMP library module (`library_module_name`, default `core`) used by the app |
| `include_sonar` | no | SonarQube Gradle plugin + `sonar_scan.sh` (`sonar_host_url`, `sonar_project_key`) |

Always included: UDF architecture (`UnidirectionalViewModel`, `BaseViewModel` with an error
boundary, `Navigator`), Koin with annotations, typed error pipeline (`AppError` → `ErrorLocalizer` →
`ErrorView`), Kermit logging, a sample list + detail feature with Mokkery tests, `docs/conventions.md`,
a `CLAUDE.md` skeleton and Dependabot.

## Updating projects generated before the androidApp split

The template moved to AGP 9, which no longer allows the Android application plugin in a Kotlin
Multiplatform module. `composeApp` is now a library, and the Android entry point is the new
`androidApp` module. `copier update` moves the files, but it does not carry over your edits to them:

- `MainActivity`, the `Application` class, `AndroidManifest.xml`, launcher resources and
  `proguard-rules.pro` moved from `composeApp/src/androidMain` to `androidApp/`.
- Android unit tests moved from `androidUnitTest` to `androidHostTest`; run `testAndroidHostTest`.
- Build the app with `:androidApp:assembleDebug`.

Re-apply your edits from git history, and update the commands in your `README.md` and `CLAUDE.md`.

## Developing the template

Template sources live in `template/`; only `*.jinja` files are rendered, everything else is copied
verbatim. Optional parts are excluded per answer through `_exclude` in `copier.yml`.
`ci/variants/*.yml` are the answer sets CI generates and builds — adding a file adds a CI variant.

```bash
copier copy --defaults --vcs-ref HEAD --data-file ci/variants/defaults.yml "$PWD" /tmp/out
```

Use an absolute source path: Copier records it in the generated `.copier-answers.yml`, and a
relative one breaks `copier update` there.
