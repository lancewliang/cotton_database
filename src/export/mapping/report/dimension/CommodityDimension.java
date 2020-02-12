package export.mapping.report.dimension;

import model.constant.Commodity;

public class CommodityDimension implements ReportDimension {
  public Commodity commodityDimension = null;

  public CommodityDimension(String commodity_dimension) throws Exception {

    this.commodityDimension = Commodity.getCommodity(commodity_dimension);
    if (this.commodityDimension == null)
      throw new Exception("not found country_dimension :" + commodity_dimension);

  }

  public Commodity getCommodity() {
    return commodityDimension;
  }

}
