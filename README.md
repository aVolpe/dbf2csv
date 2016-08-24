# Simple DBF 2 CSV converter

Requires (iryndin/jdbf)[https://github.com/iryndin/jdbf].

# Compilation

```bash
git clone https://github.com/iryndin/jdbf.git
cd jdbf
mvn install
cd ..
git clone https://github.com/aVolpe/dbf2csv
cd dbf2csv
mvn package
```

# Usage

If you has a folder with many files with the `dbf` extension in the path
`/home/myUser/dbfs/`, then you can use:

```bash
mvn exec:java -Dexec.args=/home/myUser/dbfs/
```

This will create a file with the same name but with the extension CSV in the `/home/myUser/dbfs` folder.
