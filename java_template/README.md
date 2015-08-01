# Java Template

Useful files for my Java projects.

Works perfectly fine on Windows with [MSYS](http://www.mingw.org/wiki/msys).

How to use? Within your project:

    git clone git@github.com:norswap/java_template.git
    cp java_template/makefile makefile
    # edit makefile
    # if you have a .gitignore file, rename it to own.gitignore
    java_template/update_gitignore.sh

or (does the same thing):

    git clone git@github.com:norswap/java_template.git
    java_template/setup.sh

!! To add `java_template` to your repo: 

    shopt -s dotglob # makes "*" match files with a leading dot
    git add -A java_template/*
    shopt -u dotglob # optional, undoes the first line

`git add -A java_template` will NOT work. Use `git rm --cached -f java_template`
to undo that command if you were brash and did not read this instructions
properly.

If you changed `own.gitignore`, refresh `.gitignore` with:

    java_template/update_gitignore.sh

How to update the templates? Within your project:

    (cd java_template; git pull)
    java_template/update_gitignore.sh

or (same thing):

    java_template/update.sh

Normally all scripts are marked as executable, but if you run into problems, use
`chmod +x <script>`.

# License

Do what the fuck you want.
http://www.wtfpl.net/about/

    