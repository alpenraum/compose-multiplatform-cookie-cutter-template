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
`README.md`, `CLAUDE.md`, `docs/conventions.md` and `version.properties` belong to the generated
project after the first copy; updates never overwrite them.

The template is untagged for now, so Copier uses the latest commit on `main`. Once tags exist,
Copier resolves to the newest tag instead.

## Developing the template

Template sources live in `template/`; only `*.jinja` files are rendered, everything else is copied
verbatim. Optional parts are excluded per answer through `_exclude` in `copier.yml`.
`ci/variants/*.yml` are the answer sets CI generates and builds — adding a file adds a CI variant.

```bash
copier copy --defaults --vcs-ref HEAD --data-file ci/variants/defaults.yml "$PWD" /tmp/out
```

Use an absolute source path: Copier records it in the generated `.copier-answers.yml`, and a
relative one breaks `copier update` there.
