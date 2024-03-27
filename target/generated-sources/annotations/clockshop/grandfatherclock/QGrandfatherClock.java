package clockshop.grandfatherclock;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QGrandfatherClock is a Querydsl query type for GrandfatherClock
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QGrandfatherClock extends EntityPathBase<GrandfatherClock> {

    private static final long serialVersionUID = 1572795780L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QGrandfatherClock grandfatherClock = new QGrandfatherClock("grandfatherClock");

    public final clockshop.catalog.QArticle _super;

    //inherited
    public final SetPath<String, StringPath> categories;

    public final StringPath companyName = createString("companyName");

    //inherited
    public final StringPath description;

    //inherited
    public final NumberPath<Double> discount;

    // inherited
    public final org.salespointframework.catalog.QProduct_ProductIdentifier id;

    //inherited
    public final EnumPath<org.salespointframework.quantity.Metric> metric;

    //inherited
    public final StringPath name;

    //inherited
    public final SimplePath<javax.money.MonetaryAmount> price;

    //inherited
    public final EnumPath<clockshop.catalog.Article.ArticleType> type;

    public QGrandfatherClock(String variable) {
        this(GrandfatherClock.class, forVariable(variable), INITS);
    }

    public QGrandfatherClock(Path<? extends GrandfatherClock> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QGrandfatherClock(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QGrandfatherClock(PathMetadata metadata, PathInits inits) {
        this(GrandfatherClock.class, metadata, inits);
    }

    public QGrandfatherClock(Class<? extends GrandfatherClock> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this._super = new clockshop.catalog.QArticle(type, metadata, inits);
        this.categories = _super.categories;
        this.description = _super.description;
        this.discount = _super.discount;
        this.id = _super.id;
        this.metric = _super.metric;
        this.name = _super.name;
        this.price = _super.price;
        this.type = _super.type;
    }

}

