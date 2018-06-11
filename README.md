#git 基本操作命令
git log --graph --pretty=oneline --abbrev-commit
#强制忽略提交版本库的文件
git update-index --assume-unchanged <files>
#撤销上面的操作
git update-index --no-assume-unchanged <files>
#添加所有
git add .
