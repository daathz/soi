package banking;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/banking")
public class CreditCard {

    @Path("ChargeCard")
    @Consumes({"application/x-protobuf", MediaType.APPLICATION_JSON })
    @Produces({"application/x-protobuf", MediaType.APPLICATION_JSON })
    @POST
    public Banking.ChargeCardResponse charge(Banking.ChargeCardRequest creditCard) {
        String cardNumber = creditCard.getCardNumber();
        int amount = creditCard.getAmount();
        boolean success = ((amount > 0) && ((cardNumber.length() % 2) == 0));
        return Banking.ChargeCardResponse.newBuilder().setSuccess(success).build();
    }
}
