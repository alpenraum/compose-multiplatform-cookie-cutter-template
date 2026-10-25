# Room schemas

Room exports the database schema here on every build (`room { schemaDirectory(...) }` in
`composeApp/build.gradle.kts`). Commit these files — they are what migrations are verified against.

Changing an `@Entity`:

1. Bump `version` in `@Database` (`data/db/AppDatabase.kt`).
2. Add an `AutoMigration(from = n, to = n + 1)` to `autoMigrations`, or a manual `Migration` for
   changes Room cannot infer (renames, data transforms).
3. Build once so the new `<version>.json` is exported, and commit it.

Upgrades without a migration fail on first open; only downgrades fall back to a destructive reset.
