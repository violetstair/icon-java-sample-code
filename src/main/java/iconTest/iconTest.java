package iconTest;

import foundation.icon.icx.*;
import foundation.icon.icx.data.TransactionResult;
import foundation.icon.icx.transport.http.HttpProvider;
import foundation.icon.icx.data.Bytes;
import foundation.icon.icx.data.Address;

import foundation.icon.icx.transport.jsonrpc.RpcObject;
import foundation.icon.icx.transport.jsonrpc.RpcValue;
import okhttp3.OkHttpClient;

import java.io.IOException;
import java.math.BigInteger;
import java.util.concurrent.TimeUnit;


public class iconTest {

    public static void main(String[] args) throws IOException {
        //OwnerPrivate Key
        KeyWallet scoreOwnerWallet = KeyWallet.load(new Bytes("442538b6fd295bf4b84956c861eaa5cdec146cdc08914d0c0853b6ed6c288711"));
        BigInteger nid = new BigInteger("3");


        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .readTimeout(200, TimeUnit.MILLISECONDS)
                .writeTimeout(600, TimeUnit.MILLISECONDS)
                .build();

        //Contract Address
        Address scoreAddress = new Address("cx91cb2b1c2a97ba81e9396ad69c78ae2b27460279");
        IconService iconService = new IconService(new HttpProvider(okHttpClient, "https://bicon.net.solidwallet.io/api/v3"));

        String time = String.valueOf(System.currentTimeMillis());
        String goodsno = "GOODS" + time;

        System.out.println("전송 번호 ::  " + goodsno);

        RpcObject params = new RpcObject.Builder()
                .put("_id",new RpcValue(goodsno))
                .put("_info", new RpcValue("BTS Goods"))
                .put("_celebrity", new RpcValue("BTS"))
                .put("_owner", new RpcValue("홍길동"))
                .put("_price", new RpcValue(new BigInteger("2000000")))
                .put("_date", new RpcValue(time))
                .build();

        Transaction transaction = TransactionBuilder.newBuilder()
                .nid(nid)
                //.nonce(new BigInteger("100"))
                .from(scoreOwnerWallet.getAddress())
                .to(scoreAddress)
                .stepLimit(new BigInteger("300000"))
                .timestamp(new BigInteger(Long.toString(System.currentTimeMillis() * 1000L)))
                .call("addGoods")
                .params(params)
                .build();

        System.out.println("트랜젝션 빌드 ::  OK");

        SignedTransaction signedTransaction = new SignedTransaction(transaction, scoreOwnerWallet);
        System.out.println("트랜젝션 싸이닝 ::  OK");

        // 트랜젝션 실행
        Bytes txHash = iconService.sendTransaction(signedTransaction).execute();

        MatrixTime(5000);

        System.out.println("트랜젝션 실행 :: " + txHash.toString());

        TransactionResult result = iconService.getTransactionResult(txHash).execute();

        System.out.println("TX Hash :::: " + result.getTxHash());
        System.out.println("TX Result Status :::: " + result.getStatus());
        System.out.println("TX Result Status :::: " + result.getFailure());
    }

    public static void MatrixTime(int delayTime){
        long saveTime = System.currentTimeMillis();
        long currTime = 0;

        while( currTime - saveTime < delayTime){
            currTime = System.currentTimeMillis();
        }
    }
}
