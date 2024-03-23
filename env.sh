source ~/.bashrc
export hey=`git rev-parse --show-toplevel`
export BASE_DIR=$hey
export SCRIPT_DIR=$BASE_DIR/scripts
export PATH=$BASE_DIR/scripts:$PATH

alias gohome='cd $BASE_DIR'

# prompt update
if [[ $PS1 != *"<hey>"* ]]; then
  PS1="<hey>${PS1}"
fi

