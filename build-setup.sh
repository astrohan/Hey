git clone https://github.com/chipsalliance/rocket-chip.git generators/rocket-chip
git clone https://github.com/chipsalliance/cde.git tools/cde

export home=$PWD
cd $home/generators/rocket-chip 
git checkout 836be7a92e5bd368a8a49f408a44b71c738a9a68
git submodule update --init --recursive
cd $home
find generators -name "build.sbt" | xargs rm
