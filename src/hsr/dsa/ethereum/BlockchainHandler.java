package hsr.dsa.ethereum;

import SmartContractDSAProject.SmartContractDSAProject;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthGasPrice;
import org.web3j.protocol.core.methods.response.EthGetBalance;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.core.methods.response.Web3ClientVersion;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.gas.DefaultGasProvider;
import org.web3j.utils.Convert;

import javax.swing.*;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;

public class BlockchainHandler {
    private static final String SMART_CONTRACT_ADDRESS = "0x8D18D6144d338079cD4b4f7D0981f4eE7477D49f";

    private SmartContractDSAProject smartContract;
    private String localEtherAccount;
    private String remoteEtherAccount;
    private String localPrivateKey;

    @SuppressWarnings("FieldCanBeLocal")
    private String testNetAddress = "https://rinkeby.infura.io/";
    private Web3j localWeb3;
    private Web3j smartContractWeb3;

    private String davidsEtherAccount = "0x1cE0089b18c8135B6fff8b10fC43F596A7289D83";
    private String martinsEtherAccount = "0x036FBAE35b84e03926Cf466C2Ef19165C66829b2";
    private String martinsPrivateKey = "d458a482cb2d7532aab8f76994a32351d5190bc08d661636690fae7272efeaac"; // TODO: Delete private key

    public BlockchainHandler(String localEtherAccount, String remoteEtherAccount, String localPrivateKey) {
        this.localEtherAccount = localEtherAccount;
        this.remoteEtherAccount = remoteEtherAccount;
        this.localPrivateKey = localPrivateKey;
        System.out.println("BlockchainHandler: " + localEtherAccount);
        System.out.println("BlockchainHandler: " + remoteEtherAccount);

        localWeb3 = Web3j.build(new HttpService(testNetAddress + localEtherAccount));
        smartContractWeb3 = Web3j.build(new HttpService(testNetAddress + SMART_CONTRACT_ADDRESS));

        System.out.println("Your Balance: " + getBalanceFromAccount(localEtherAccount) + " WEI");
        System.out.println("Enemys Balance: " + getBalanceFromAccount(remoteEtherAccount) + " WEI");


        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                getDeployedSmartContract();
            }
        });
        //getDeployedSmartContract();
    }

    private BigInteger getBalanceFromAccount(String account) {
        EthGetBalance balance = null;
        try {
            balance = localWeb3.ethGetBalance(account, DefaultBlockParameterName.LATEST).send();
        } catch (IOException e) {
            e.printStackTrace();
        }
        assert balance != null;
        BigDecimal balanceInEther = Convert.fromWei(balance.getBalance().toString(), Convert.Unit.WEI);
        return balanceInEther.toBigInteger();
    }

    private void getDeployedSmartContract() {
        Credentials creds = Credentials.create(localPrivateKey);
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                smartContract = SmartContractDSAProject.load(SMART_CONTRACT_ADDRESS, smartContractWeb3, creds, new DefaultGasProvider());

                try {
                    if (!smartContract.isValid()) {
                        System.out.println("Could not load smart contract!!");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });


    }

    public void storeAmountInBlockchain(BigDecimal gambleAmountETH) throws Exception {
        BigInteger gambleAmountWEI = gambleAmountETH.multiply(new BigDecimal("1E18")).toBigInteger();
        if (gambleAmountWEI.compareTo(getBalanceFromAccount(localEtherAccount)) != -1) {
            System.out.println("You have not enough ethers!");
            throw new Exception("You have not enough ethers!");
        }
        if (gambleAmountWEI.compareTo(getBalanceFromAccount(remoteEtherAccount)) != -1) {
            System.out.println("Your enemy has not enough ether!");
            throw new Exception("Your enemy has not enough ether!");
        }


        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                TransactionReceipt loadAmountToContract = null;
                try {
                    loadAmountToContract = smartContract.start(gambleAmountWEI).send();
                    if (!loadAmountToContract.isStatusOK()) {
                        System.out.println("Could not load ethers into the smart Contract!!");
                        throw new Exception("Could not load ethers into the smart Contract!!");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

    }

    // Can only be called from the looser, because he has to pay the winner.
    public void startTransaction() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    TransactionReceipt payAmount = smartContract.payAmountToEnemty(remoteEtherAccount).send();
                    if (!payAmount.isStatusOK()) {
                        System.out.println("Could not pay ethers to the winner!");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

    public void printClientVersions() {
        try {
            Web3ClientVersion localClientVersion = localWeb3.web3ClientVersion().send();
            System.out.println("Local client version: " + localClientVersion.getWeb3ClientVersion());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public BigInteger getGasPrice() {
        try {
            EthGasPrice price = localWeb3.ethGasPrice().send();
            return price.getGasPrice();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new BigInteger(String.valueOf(0));
    }

    public void printGasPrice() {
        System.out.println("Gas price: " + DefaultGasProvider.GAS_PRICE);
        System.out.println("Gas limit: " + DefaultGasProvider.GAS_LIMIT);
    }
}
