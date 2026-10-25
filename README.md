# compose-multiplatform-cookie-cutter-template

A [Copier](https://copier.readthedocs.io) template for Compose Multiplatform apps targeting
Android and iOS, with optional desktop.

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

## Developing the template

Template sources live in `template/`; only `*.jinja` files are rendered, everything else is copied
verbatim. Optional parts are excluded per answer through `_exclude` in `copier.yml`.
`ci/variants/*.yml` are the answer sets CI generates and builds — adding a file adds a CI variant.

```bash
copier copy --defaults --vcs-ref HEAD --data-file ci/variants/defaults.yml "$PWD" /tmp/out
```

Use an absolute source path: Copier records it in the generated `.copier-answers.yml`, and a
relative one breaks `copier update` there.
