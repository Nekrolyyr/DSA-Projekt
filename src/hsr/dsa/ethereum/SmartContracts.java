package hsr.dsa.ethereum;

public class SmartContracts {




    /*
    Smart Contract kompilieren:
        solc <sol> --bin --abi --optimize -o <output>
    Beispiel: solc SmartContractDSAProject.sol --bin --abi --optimize --overwrite -o SmartContractDSAProject

    Wrapper für Java erstellen: (funktioniert nur im Ordner von web3j)
        ./web3j solidity generate -a=<abiFile> -b=<binFile> -o=<destinationFileDir> -p=<packageName>
    Beispiel: ./web3j solidity generate -a SmartContracts/SmartContractDSAProject/SmartContractDSAProject.abi -b SmartContracts/SmartContractDSAProject/SmartContractDSAProject.abi -o . -p SmartContractDSAProject


    Dann wird eine .java Datei generiert, welche man in das Projekt kopieren kann.
    cp SmartContractDSAProject/SmartContractDSAProject.java /home/martin/Studium/Distributed_Systems_Advanced/DSA_Project/src/hsr/dsa/ethereum/
     */




}
