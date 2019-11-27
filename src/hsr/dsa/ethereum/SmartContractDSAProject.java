package SmartContractDSAProject;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.RemoteFunctionCall;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.Contract;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.gas.ContractGasProvider;

/**
 * <p>Auto generated code.
 * <p><strong>Do not modify!</strong>
 * <p>Please use the <a href="https://docs.web3j.io/command_line.html">web3j command line tools</a>,
 * or the org.web3j.codegen.SolidityFunctionWrapperGenerator in the 
 * <a href="https://github.com/web3j/web3j/tree/master/codegen">codegen module</a> to update.
 *
 * <p>Generated with web3j version 4.5.5.
 */
@SuppressWarnings("rawtypes")
public class SmartContractDSAProject extends Contract {
    //private static final String BINARY = "[{\"constant\":false,\"inputs\":[{\"internalType\":\"address payable\",\"name\":\"player\",\"type\":\"address\"}],\"name\":\"payAmountToEnemty\",\"outputs\":[],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"internalType\":\"uint256\",\"name\":\"amount\",\"type\":\"uint256\"}],\"name\":\"setGambleAmount\",\"outputs\":[],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[],\"name\":\"start\",\"outputs\":[],\"payable\":true,\"stateMutability\":\"payable\",\"type\":\"function\"}]";
    private static final String BINARY = "608060405234801561001057600080fd5b506101b5806100206000396000f3fe6080604052600436106100345760003560e01c80630a32b84d146100395780639b288ef81461008a578063be9a6555146100c5575b600080fd5b34801561004557600080fd5b506100886004803603602081101561005c57600080fd5b81019080803573ffffffffffffffffffffffffffffffffffffffff1690602001909291905050506100cf565b005b34801561009657600080fd5b506100c3600480360360208110156100ad57600080fd5b8101908080359060200190929190505050610130565b005b6100cd61013a565b005b8073ffffffffffffffffffffffffffffffffffffffff166108fc3073ffffffffffffffffffffffffffffffffffffffff16319081150290604051600060405180830381858888f1935050505015801561012c573d6000803e3d6000fd5b5050565b8060008190555050565b34600160003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020019081526020016000208190555056fea265627a7a72315820fdda9590ce7b5b24e6ccbce0d134feeeb010f596c2d351c9e8f881918cd6f97964736f6c634300050b0032";
    public static final String FUNC_PAYAMOUNTTOENEMTY = "payAmountToEnemty";

    public static final String FUNC_SETGAMBLEAMOUNT = "setGambleAmount";

    public static final String FUNC_START = "start";

    @Deprecated
    protected SmartContractDSAProject(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected SmartContractDSAProject(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, credentials, contractGasProvider);
    }

    @Deprecated
    protected SmartContractDSAProject(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    protected SmartContractDSAProject(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public RemoteFunctionCall<TransactionReceipt> payAmountToEnemty(String player) {
        final Function function = new Function(
                FUNC_PAYAMOUNTTOENEMTY, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, player)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> setGambleAmount(BigInteger amount) {
        final Function function = new Function(
                FUNC_SETGAMBLEAMOUNT, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(amount)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> start(BigInteger weiValue) {
        final Function function = new Function(
                FUNC_START, 
                Arrays.<Type>asList(), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function, weiValue);
    }

    @Deprecated
    public static SmartContractDSAProject load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new SmartContractDSAProject(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    @Deprecated
    public static SmartContractDSAProject load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new SmartContractDSAProject(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static SmartContractDSAProject load(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        return new SmartContractDSAProject(contractAddress, web3j, credentials, contractGasProvider);
    }

    public static SmartContractDSAProject load(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return new SmartContractDSAProject(contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public static RemoteCall<SmartContractDSAProject> deploy(Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        return deployRemoteCall(SmartContractDSAProject.class, web3j, credentials, contractGasProvider, BINARY, "");
    }

    @Deprecated
    public static RemoteCall<SmartContractDSAProject> deploy(Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(SmartContractDSAProject.class, web3j, credentials, gasPrice, gasLimit, BINARY, "");
    }

    public static RemoteCall<SmartContractDSAProject> deploy(Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return deployRemoteCall(SmartContractDSAProject.class, web3j, transactionManager, contractGasProvider, BINARY, "");
    }

    @Deprecated
    public static RemoteCall<SmartContractDSAProject> deploy(Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(SmartContractDSAProject.class, web3j, transactionManager, gasPrice, gasLimit, BINARY, "");
    }
}
