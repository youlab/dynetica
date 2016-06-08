#!/bin/csh
foreach javaFile ( *.java )
 sed -e "s/evirus/dynetica/g; s/Evirus/Dynetica/g" $javaFile > tmp
 mv tmp  $javaFile
end
