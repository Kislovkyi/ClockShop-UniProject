package clockshop.order;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QShopOrder is a Querydsl query type for ShopOrder
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QShopOrder extends EntityPathBase<ShopOrder> {

    private static final long serialVersionUID = 2022780238L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QShopOrder shopOrder = new QShopOrder("shopOrder");

    public final org.salespointframework.order.QOrder _super;

    public final StringPath address = createString("address");

    //inherited
    public final ListPath<org.salespointframework.order.ChargeLine.AttachedChargeLine, org.salespointframework.order.QChargeLine_AttachedChargeLine> attachedChargeLines;

    //inherited
    public final ListPath<org.salespointframework.order.ChargeLine, org.salespointframework.order.QChargeLine> chargeLines;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> dateCreated;

    public final StringPath email = createString("email");

    public final StringPath forename = createString("forename");

    public final StringPath name = createString("name");

    // inherited
    public final org.salespointframework.order.QOrder_OrderIdentifier orderIdentifier;

    //inherited
    public final ListPath<org.salespointframework.order.OrderLine, org.salespointframework.order.QOrderLine> orderLines;

    //inherited
    public final EnumPath<org.salespointframework.order.OrderStatus> orderStatus;

    //inherited
    public final SimplePath<org.salespointframework.payment.PaymentMethod> paymentMethod;

    public final StringPath telephone = createString("telephone");

    // inherited
    public final org.salespointframework.useraccount.QUserAccount_UserAccountIdentifier userAccountIdentifier;

    public QShopOrder(String variable) {
        this(ShopOrder.class, forVariable(variable), INITS);
    }

    public QShopOrder(Path<? extends ShopOrder> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QShopOrder(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QShopOrder(PathMetadata metadata, PathInits inits) {
        this(ShopOrder.class, metadata, inits);
    }

    public QShopOrder(Class<? extends ShopOrder> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this._super = new org.salespointframework.order.QOrder(type, metadata, inits);
        this.attachedChargeLines = _super.attachedChargeLines;
        this.chargeLines = _super.chargeLines;
        this.dateCreated = _super.dateCreated;
        this.orderIdentifier = _super.orderIdentifier;
        this.orderLines = _super.orderLines;
        this.orderStatus = _super.orderStatus;
        this.paymentMethod = _super.paymentMethod;
        this.userAccountIdentifier = _super.userAccountIdentifier;
    }

}

