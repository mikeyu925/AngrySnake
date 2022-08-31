scp dist/js/*.js springboot:angrysnake/acapp/
scp dist/css/*.css springboot:angrysnake/acapp/

ssh springboot 'cd angrysnake/acapp && ./rename.sh'
