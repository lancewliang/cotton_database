package ant.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ant.chemicalfiber.price.GetCottonChinaCNChemicalfiberPrice;
import ant.cotton.consumption.GetINDConsumptionMonth;
import ant.cotton.consumption.GetUSDAConsumptionMonth;
import ant.cotton.custom.GetCustomCottonChinaMonthly;
import ant.cotton.custom.GetCustomExpINDMonthly;
import ant.cotton.custom.GetCustomImpINDMonthly;
import ant.cotton.custom.GetCustomPKMonthly;
import ant.cotton.custom.GetCustomUSDAExport;
import ant.cotton.ginn.GetCNGinnAndSalesWeely;
import ant.cotton.ginn.GetPCGAGinnAndSalesHalfMonth;
import ant.cotton.ginn.GetUSDAGinnHalfMonth;
import ant.cotton.gov.AdjustCNMonth;
import ant.cotton.gov.GetCNBuyHistoryWeb;
import ant.cotton.gov.GetCNSellHistoryWeb;
import ant.cotton.price.future.CNZhengZhouPriceFuturePrice;
import ant.cotton.price.future.MCXINDFuturePrice;
import ant.cotton.price.future.USICEPriceFuturePrice;
import ant.cotton.price.port.CNCottonChinaPortPrice;
import ant.cotton.price.port.GetCNCottonCotlook;
import ant.cotton.price.spot.BrazilSoptRate;
import ant.cotton.price.spot.CHINACCSpotRates;
import ant.cotton.price.spot.GetPCGASpotRates;
import ant.cotton.price.spot.IndiaSpotRates;
import ant.cotton.price.spot.USDASpotRates;
import ant.cotton.production.GetCNCottonProduction;
import ant.cotton.production.GetUSDAProductionMonth;
import ant.cotton.stock.GetCNCycleStockMonthly;
import ant.cotton.stock.GetCNStockMonthly;
import ant.cotton.stock.GetCZCZStockDayly;
import ant.cotton.stock.GetINDStockMonthly;
import ant.cotton.wasde.AntUSDA_WASDE;
import ant.cotton.wasde.GetCottonChinaWASDE;
import ant.cotton.wasde.GetINDWASDEAndCope;
import ant.exrate.MonthlyExchangeRate;
import ant.restaurant.GetBaiduRestAurant;
import ant.restaurant.GetELMRestAurant;
import ant.restaurant.GetMeituanRestAurant;
import ant.textile.custom.GetCNClothingCustomData;
import ant.textile.custom.GetCNGeneralCustomData;
import ant.textile.custom.GetCNTextileCustomData;
import ant.weather.GetBrWeatherDay;
import ant.weather.GetWeatherDay;
import ant.weather.GetWeatherGfs_500_loop;
import ant.yarn.custom.GetCustomYarnChinaMonthly;
import ant.yarn.custom.GetINDYarnMonthly;
import ant.yarn.price.spot.GetCottonChinaCNYarn;
import ant.yarn.price.spot.GetCottonChinaCotlookYarn;

public class AntManger {
  public static String UPDATEBY = "ant";
  static List<AntInstruction> list = new ArrayList<AntInstruction>();
  static Map<String, DayAnt> map = new HashMap<String, DayAnt>();
  static {

    putAnter("GetWeatherDay.us", new GetWeatherDay("us"));
    putAnter("GetWeatherDay.ar", new GetWeatherDay("ar"));
    putAnter("GetWeatherDay.br", new GetWeatherDay("br"));
    putAnter("GetWeatherDay.my", new GetWeatherDay("my"));
    putAnter("GetWeatherDay.id", new GetWeatherDay("id"));
    putAnter("GetWeatherDay.in", new GetWeatherDay("in"));
    putAnter("GetWeatherDay.th", new GetWeatherDay("th"));
    putAnter("GetWeatherDay.vn", new GetWeatherDay("vn"));
    List<DayAnt> ginns_us = new ArrayList<DayAnt>();
    ginns_us.add(new GetUSDAGinnHalfMonth());

    ginns_us.add(new GetUSDAConsumptionMonth());
    ginns_us.add(new GetCustomUSDAExport());
    ginns_us.add(new AntUSDA_WASDE());
    ginns_us.add(new GetUSDAProductionMonth());

    putAnters("cotton.economics.us", ginns_us);
    List<DayAnt> ginns_ind = new ArrayList<DayAnt>();
    ginns_ind.add(new GetINDStockMonthly());
    ginns_ind.add(new GetINDConsumptionMonth());
    // ginns_ind.add(new GetINDGinnDay());
    ginns_ind.add(new GetCustomExpINDMonthly());
    ginns_ind.add(new GetCustomImpINDMonthly());
    ginns_ind.add(new GetPCGAGinnAndSalesHalfMonth());
    ginns_ind.add(new GetPCGASpotRates());
    ginns_ind.add(new GetCustomPKMonthly());
    ginns_ind.add(new GetINDWASDEAndCope());
    ginns_ind.add(new GetINDYarnMonthly());

    putAnters("cotton.economics.ind", ginns_ind);

    List<DayAnt> ginns = new ArrayList<DayAnt>();
    ginns.add(new GetCNGinnAndSalesWeely());
    ginns.add(new GetCNStockMonthly());
    ginns.add(new GetCustomCottonChinaMonthly());
    ginns.add(new GetCottonChinaWASDE());
    ginns.add(new GetCustomYarnChinaMonthly());
    ginns.add(new GetCNClothingCustomData());
    ginns.add(new GetCNGeneralCustomData());
    ginns.add(new GetCNTextileCustomData());
    ginns.add(new GetCNCycleStockMonthly());
    ginns.add(new GetCZCZStockDayly());

    //
    ginns.add(new GetCNCottonProduction());
    ginns.add(new GetCNBuyHistoryWeb());
    ginns.add(new GetCNSellHistoryWeb());
    ginns.add(new AdjustCNMonth());
    putAnters("cotton.economics.cn", ginns);

    List<DayAnt> govHistroy = new ArrayList<DayAnt>();

    //
    govHistroy.add(new CNCottonChinaPortPrice());
    govHistroy.add(new CHINACCSpotRates());

    govHistroy.add(new CNZhengZhouPriceFuturePrice());
    govHistroy.add(new USICEPriceFuturePrice());
    govHistroy.add(new IndiaSpotRates());
    govHistroy.add(new GetCNCottonCotlook());
    govHistroy.add(new GetCottonChinaCNYarn());
    govHistroy.add(new GetCottonChinaCotlookYarn());
    govHistroy.add(new GetCottonChinaCNChemicalfiberPrice());
    govHistroy.add(new MonthlyExchangeRate());

    putAnters("cotton.price.cn", govHistroy);
    List<DayAnt> priceother = new ArrayList<DayAnt>();
    priceother.add(new USDASpotRates());

    priceother.add(new BrazilSoptRate());
    priceother.add(new MCXINDFuturePrice());
    putAnters("cotton.price.other", priceother);
    List<DayAnt> otherwether = new ArrayList<DayAnt>();
    otherwether.add(new GetBrWeatherDay());
    otherwether.add(new GetWeatherGfs_500_loop());
    putAnters("otherwether", otherwether);
    List<DayAnt> restansts = new ArrayList<DayAnt>();
    restansts.add(new GetELMRestAurant()); 
    restansts.add(new GetBaiduRestAurant()); 
     
 
    putAnters("restansts", restansts);
    List<DayAnt> restansts2= new ArrayList<DayAnt>();
  
    restansts2.add(new GetMeituanRestAurant()); 
 
    putAnters("restansts2", restansts2);
  }

  private static void putAnters(String name, List<DayAnt> getWeatherDays) {
    AntInstruction instrauction = new AntInstruction(name, getWeatherDays);
    list.add(instrauction);
  }

  private static void putAnter(String name, DayAnt obj) {
    List<DayAnt> item = new ArrayList<DayAnt>();
    item.add(obj);
    AntInstruction instrauction = new AntInstruction(name, item);

    list.add(instrauction);
  }

  public static List<AntInstruction> getAnters() {
    return list;
  }

  public static AntInstruction getAnters(String name) {
    for (AntInstruction a : list) {
      if (a.getName().equals(name)) {
        return a;
      }
    }
    return null;
  }

  public static DayAnt getAnt(String name) {
    return map.get(name.toLowerCase());
  }
}
