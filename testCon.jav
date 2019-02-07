package gradle.java.sample;

import java.math.BigDecimal;
import java.math.RoundingMode;

import gradle.java.bo.RecordingProduct;
import gradle.java.enums.FreeProductTypeEnum;
import gradle.java.enums.PriceBonusTypeEnum;
import gradle.java.enums.RecordingTypeEnum;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

public class UnitPriceCalculator {
	
	public RecordingProduct calculateUnitPrice(RecordingProduct recordingProduct) {
		
		Long recodingTypCd =   recordingProduct.getRecodingTypCd();
		
		switch(RecordingTypeEnum.getValue(recodingTypCd)) {
			case STANDARD:
				System.out.println("Standard Type");
				recordingProduct = calculateStandardPrice(recordingProduct);
			break;
			case BONUS:
				recordingProduct = calculateBonusTypePrice(recordingProduct);
				break;
			case FREE:
				recordingProduct = calculateFreeTypePrice(recordingProduct);
				break;
			case NOTFOUND:
				break;
			default:
				break;
		}
		
		return recordingProduct;		
	}
	
	private RecordingProduct calculateStandardPrice(RecordingProduct recordingProduct) {
		
		Double recordPrice = recordingProduct.getRecordPrice();
		Double defContent = recordingProduct.getDefContent();
		Double recordQty = recordingProduct.getRecordQty();
		Double recordPrdContent = recordingProduct.getRecordPrdContent();
		
		
		Double unitPrice = standardPrice(recordPrice, defContent, recordQty, recordPrdContent);
		
		recordingProduct.setUnitPriceWithPromo(unitPrice);
		recordingProduct.setUnitPriceWithoutPromo(unitPrice);
		
		return recordingProduct;
	}
	
	private RecordingProduct calculateBonusTypePrice(RecordingProduct recordingProduct) {
		Long priceBonusType =   recordingProduct.getPriceBonusType();
		
		switch(PriceBonusTypeEnum.getValue(priceBonusType)) {
			case PIECE:
				recordingProduct = calculateBonusPiecePart(recordingProduct);
				break;
			case TOTAL:
				recordingProduct = calculateBonusTotalPart(recordingProduct);
				break;
			default:
				break;
		}
		return recordingProduct;
	}
	
	private RecordingProduct calculateFreeTypePrice(RecordingProduct recordingProduct) {
		Long freeProductType =   recordingProduct.getFreeProductType();
		switch(FreeProductTypeEnum.getValue(freeProductType)) {
			case PART:
				recordingProduct = calculateFreePartType(recordingProduct);
				break;
			case EXTRA:
				recordingProduct = calculateFreeExtraPart(recordingProduct);
				break;
			default:
				break;
		}
		return recordingProduct;
	}
	
	private RecordingProduct calculateFreePartType(RecordingProduct recordingProduct) {
		Double freePrdQty = recordingProduct.getFreePrdQty();
		Double freePrdContent = recordingProduct.getFreePrdContent();
		Double freePrdContentPercent = recordingProduct.getFreePrdContentPercent();
		
		Double recordPrice = recordingProduct.getRecordPrice();
		Double defContent = recordingProduct.getDefContent();
		Double recordQty = recordingProduct.getRecordQty();
		Double recordPrdContent = recordingProduct.getRecordPrdContent();
		
		Double unitPriceIncludingPromo = null;
		Double unitPriceExcludingPromo = null;
		if(freePrdQty != null && freePrdQty > 0) {
			unitPriceExcludingPromo = priceByFreePartQtyExcludingPromo(recordPrice, defContent, recordQty, recordPrdContent, freePrdQty);
			unitPriceIncludingPromo = priceByFreePartQtyIncludingPromo(recordPrice, defContent, recordQty, recordPrdContent, freePrdQty);
		} else if(freePrdContent != null && freePrdContent > 0) {
			unitPriceExcludingPromo = priceByFreePartCntAmtExcludingPromo(recordPrice, defContent, recordQty, recordPrdContent, freePrdContent);
			unitPriceIncludingPromo = priceByFreePartCntAmtIncludingPromo(recordPrice, defContent, recordQty, recordPrdContent, freePrdContent);
		} else if(freePrdContentPercent != null && freePrdContentPercent > 0) {
			unitPriceExcludingPromo = priceByFreePartCntPerExcludingPromo(recordPrice, defContent, recordQty, recordPrdContent, freePrdContentPercent);
			unitPriceIncludingPromo = priceByFreePartCntAmtIncludingPromo(recordPrice, defContent, recordQty, recordPrdContent, freePrdContentPercent);
		} else {
			
		}
		
		unitPriceIncludingPromo = round(unitPriceIncludingPromo, 4);
		unitPriceExcludingPromo = round(unitPriceExcludingPromo, 4);
		
		recordingProduct.setUnitPriceWithPromo(unitPriceIncludingPromo);
		recordingProduct.setUnitPriceWithoutPromo(unitPriceExcludingPromo);
		
		return recordingProduct;
	}
	
	private RecordingProduct calculateFreeExtraPart(RecordingProduct recordingProduct) {
		Double freePrdQty = recordingProduct.getFreePrdQty();
		Double freePrdContent = recordingProduct.getFreePrdContent();
		Double freePrdContentPercent = recordingProduct.getFreePrdContentPercent();
		
		Double recordPrice = recordingProduct.getRecordPrice();
		Double defContent = recordingProduct.getDefContent();
		Double recordQty = recordingProduct.getRecordQty();
		Double recordPrdContent = recordingProduct.getRecordPrdContent();
		
		Double unitPriceIncludingPromo = null;
		Double unitPriceExcludingPromo = null;
		if(freePrdQty != null && freePrdQty > 0) {
			unitPriceExcludingPromo = priceByFreeExtraQtyExcludingPromo(recordPrice, defContent, recordQty, recordPrdContent, freePrdQty);
			unitPriceIncludingPromo = priceByFreeExtraQtyIncludingPromo(recordPrice, defContent, recordQty, recordPrdContent, freePrdQty);
		} else if(freePrdContent != null && freePrdContent > 0) {
			unitPriceExcludingPromo = priceByFreeExtraCntAmtExcludingPromo(recordPrice, defContent, recordQty, recordPrdContent, freePrdContent);
			unitPriceIncludingPromo = priceByFreeExtraCntAmtIncludingPromo(recordPrice, defContent, recordQty, recordPrdContent, freePrdContent);
		} else if(freePrdContentPercent != null && freePrdContentPercent > 0) {
			unitPriceExcludingPromo = priceByFreeExtraCntPerExcludingPromo(recordPrice, defContent, recordQty, recordPrdContent, freePrdContentPercent);
			unitPriceIncludingPromo = priceByFreeExtraCntAmtIncludingPromo(recordPrice, defContent, recordQty, recordPrdContent, freePrdContentPercent);
		} else {
			
		}	
		
		unitPriceIncludingPromo = round(unitPriceIncludingPromo, 4);
		unitPriceExcludingPromo = round(unitPriceExcludingPromo, 4);
		
		recordingProduct.setUnitPriceWithPromo(unitPriceIncludingPromo);
		recordingProduct.setUnitPriceWithoutPromo(unitPriceExcludingPromo);
		
		return recordingProduct;
	}
	
	private RecordingProduct calculateBonusPiecePart (RecordingProduct recordingProduct) {
		Double bonusRedAmount = recordingProduct.getBonusRedAmount();
		Double bonusRedAmountPer = recordingProduct.getBonusRedAmountPer();
		
		Double recordPrice = recordingProduct.getRecordPrice();
		Double defContent = recordingProduct.getDefContent();
		Double recordQty = recordingProduct.getRecordQty();
		Double recordPrdContent = recordingProduct.getRecordPrdContent();
		
		Double unitPrice = null;
		if(bonusRedAmount != null && bonusRedAmount > 0) {
			unitPrice = priceByBonusPieceAmtIncludingExcludingPromo(recordPrice, defContent, recordPrdContent, bonusRedAmount);
			//unitPriceIncludingPromo = priceByBonusPieceAmtIncludingExcludingPromo(recordPrice, defContent, recordPrdContent, bonusRedAmount);
		} else if(bonusRedAmountPer != null && bonusRedAmountPer > 0) {
			unitPrice = priceByBonusPiecePerIncludingExcludingPromo(recordPrice, defContent, recordQty, recordPrdContent, bonusRedAmountPer);
			//unitPriceIncludingPromo = priceByBonusPiecePerIncludingExcludingPromo(recordPrice, defContent, recordQty, recordPrdContent, bonusRedAmountPer);
		} else {
			
		}	
		
		unitPrice = round(unitPrice, 4);
		
		recordingProduct.setUnitPriceWithPromo(unitPrice);
		recordingProduct.setUnitPriceWithoutPromo(unitPrice);
		
		return recordingProduct;
	}
	
	private RecordingProduct calculateBonusTotalPart (RecordingProduct recordingProduct) {
		Double bonusRedAmount = recordingProduct.getBonusRedAmount();
		Double bonusRedAmountPer = recordingProduct.getBonusRedAmountPer();
		
		Double recordPrice = recordingProduct.getRecordPrice();
		Double defContent = recordingProduct.getDefContent();
		Double recordQty = recordingProduct.getRecordQty();
		Double recordPrdContent = recordingProduct.getRecordPrdContent();
		
		Double unitPrice = null;
		if(bonusRedAmount != null && bonusRedAmount > 0) {
			unitPrice = priceByBonusTotalAmtIncludingExcludingPromo(recordPrice, defContent,recordQty, recordPrdContent, bonusRedAmount);
		} else if(bonusRedAmountPer != null && bonusRedAmountPer > 0) {
			unitPrice = priceByBonusTotalPerIncludingExcludingPromo(recordPrice, defContent, recordQty, recordPrdContent, bonusRedAmountPer);
		} else {
			
		}
		
		unitPrice = round(unitPrice, 4);
		recordingProduct.setUnitPriceWithPromo(unitPrice);
		recordingProduct.setUnitPriceWithoutPromo(unitPrice);
		
		return recordingProduct;
	}
	
	public BigDecimal standardPrice(BigDecimal recordPrice, Double defContent, Double recordQty, Double recordPrdContent) {
		
		/*Expression calc = new ExpressionBuilder(" (a * b)  /  (c * d) ")
				 .variables("a","b", "c", "d")
				.build()
				.setVariable("a", recordPrice)
		        .setVariable("b", defContent)
		        .setVariable("c", recordQty)
		        .setVariable("d", recordPrdContent);
		        
		Double result = calc.evaluate();
		//(recordPrice * defContent) / (recordQty * recordPrdContent);
		BigDecimal result1 = BigDecimal.valueOf(result);*/
		
		BigDecimal defContentEnurator = BigDecimal.valueOf(defContent);
		
		BigDecimal divisorVal = BigDecimal.valueOf(recordQty * recordPrdContent);
		
		BigDecimal result = recordPrice.multiply(defContentEnurator);
		result = result.divide(divisorVal);
		return result;
	}
	
	/**
	 * @param recordPrice
	 * @param defContent
	 * @param recordQty
	 * @param recordPrdContent
	 * @param freePrdQty
	 * @return
	 */
	public Double priceByFreePartQtyExcludingPromo(Double recordPrice, Double defContent, Double recordQty, Double recordPrdContent, Double freePrdQty) {
		
		return (recordPrice * defContent) / ((recordQty - freePrdQty) * recordPrdContent);
	}
	
	/**
	 * @param recordPrice
	 * @param defContent
	 * @param recordQty
	 * @param recordPrdContent
	 * @param freePrdQty
	 * @return
	 */
	public Double priceByFreePartQtyIncludingPromo(Double recordPrice, Double defContent, Double recordQty, Double recordPrdContent, Double freePrdQty) {
		return (recordPrice * defContent) / (recordQty * recordPrdContent);
	}
	
	/**
	 * @param recordPrice
	 * @param defContent
	 * @param recordQty
	 * @param recordPrdContent
	 * @param freePrdQty
	 * @return
	 */
	public Double priceByFreeExtraQtyExcludingPromo(Double recordPrice, Double defContent, Double recordQty, Double recordPrdContent, Double freePrdQty) {
		return (recordPrice * defContent) / (recordQty * recordPrdContent);
	}
	
	/**
	 * @param recordPrice
	 * @param defContent
	 * @param recordQty
	 * @param recordPrdContent
	 * @param freePrdQty
	 * @return
	 */
	public Double priceByFreeExtraQtyIncludingPromo(Double recordPrice, Double defContent, Double recordQty, Double recordPrdContent, Double freePrdQty) {
		return (recordPrice * defContent) / ((recordQty + freePrdQty) * recordPrdContent);
	}
	
	/**
	 * @param recordPrice
	 * @param defContent
	 * @param recordQty
	 * @param recordPrdContent
	 * @param freePrdContent
	 * @return
	 */
	public Double priceByFreePartCntAmtExcludingPromo(Double recordPrice, Double defContent, Double recordQty, Double recordPrdContent, Double freePrdContent) {
		
		return (recordPrice * defContent) / ( recordQty * (recordPrdContent - freePrdContent));
	}
	
	/**
	 * @param recordPrice
	 * @param defContent
	 * @param recordQty
	 * @param recordPrdContent
	 * @param freePrdContent
	 * @return
	 */
	public Double priceByFreePartCntAmtIncludingPromo(Double recordPrice, Double defContent, Double recordQty, Double recordPrdContent, Double freePrdContent) {
		
		return (recordPrice * defContent) / ( recordQty * recordPrdContent);
	}
	
	/**
	 * @param recordPrice
	 * @param defContent
	 * @param recordQty
	 * @param recordPrdContent
	 * @param freePrdContent
	 * @return
	 */
	public Double priceByFreeExtraCntAmtExcludingPromo(Double recordPrice, Double defContent, Double recordQty, Double recordPrdContent, Double freePrdContent) {
		
		return (recordPrice * defContent) / ( recordQty * recordPrdContent);
	}
	
	/**
	 * @param recordPrice
	 * @param defContent
	 * @param recordQty
	 * @param recordPrdContent
	 * @param freePrdContent
	 * @return
	 */
	public Double priceByFreeExtraCntAmtIncludingPromo(Double recordPrice, Double defContent, Double recordQty, Double recordPrdContent, Double freePrdContent) {
		
		return (recordPrice * defContent) / ( recordQty * (recordPrdContent + freePrdContent));
	}
	
	/**
	 * @param recordPrice
	 * @param defContent
	 * @param recordQty
	 * @param recordPrdContent
	 * @param freePrdContentPercent
	 * @return
	 */
	public Double priceByFreePartCntPerExcludingPromo(Double recordPrice, Double defContent, Double recordQty, Double recordPrdContent, Double freePrdContentPercent) {
		
		return (recordPrice * defContent) / ( recordQty * (recordPrdContent -  ( (freePrdContentPercent/ 100) *  recordPrdContent ) ));
	}
	
	/**
	 * @param recordPrice
	 * @param defContent
	 * @param recordQty
	 * @param recordPrdContent
	 * @param freePrdContentPercent
	 * @return
	 */
	public Double priceByFreePartCntPerIncludingPromo(Double recordPrice, Double defContent, Double recordQty, Double recordPrdContent, Double freePrdContentPercent) {
		
		return (recordPrice * defContent) / ( recordQty * recordPrdContent);
	}
	
	/**
	 * @param recordPrice
	 * @param defContent
	 * @param recordQty
	 * @param recordPrdContent
	 * @param freePrdContentPercent
	 * @return
	 */
	public Double priceByFreeExtraCntPerExcludingPromo(Double recordPrice, Double defContent, Double recordQty, Double recordPrdContent, Double freePrdContentPercent) {
		
		return (recordPrice * defContent) / ( recordQty * recordPrdContent);
	}
	
	/**
	 * @param recordPrice
	 * @param defContent
	 * @param recordQty
	 * @param recordPrdContent
	 * @param freePrdContentPercent
	 * @return
	 */
	public Double priceByFreeExtraCntPerIncludingPromo(Double recordPrice, Double defContent, Double recordQty, Double recordPrdContent, Double freePrdContentPercent) {
		
		return (recordPrice * defContent) / ( recordQty * (recordPrdContent *  ( (1+freePrdContentPercent/ 100) ) ));
	}
	
	/**
	 * @param recordPrice
	 * @param defContent
	 * @param recordPrdContent
	 * @param bonusRedAmount
	 * @return
	 */
	public Double priceByBonusPieceAmtIncludingExcludingPromo(Double recordPrice, Double defContent, Double recordPrdContent, Double bonusRedAmount) {
		
		return ( (recordPrice - bonusRedAmount) * defContent) /  recordPrdContent;
	}
	
	
	/**
	 * @param recordPrice
	 * @param defContent
	 * @param recordQty
	 * @param recordPrdContent
	 * @param bonusRedAmount
	 * @return
	 */
	public Double priceByBonusTotalAmtIncludingExcludingPromo(Double recordPrice, Double defContent, Double recordQty, Double recordPrdContent, Double bonusRedAmount) {
		
		return ( (recordPrice - bonusRedAmount) * defContent) /  ( recordQty * recordPrdContent );
	}
	
	
	/**
	 * @param recordPrice
	 * @param defContent
	 * @param recordQty
	 * @param recordPrdContent
	 * @param bonusRedAmountPer
	 * @return
	 */
	public Double priceByBonusPiecePerIncludingExcludingPromo(Double recordPrice, Double defContent, Double recordQty, Double recordPrdContent, Double bonusRedAmountPer) {
		
		return ( recordPrice * ( 1 - (bonusRedAmountPer/ 100) ) * defContent) /  recordPrdContent; 
	}
	
	/**
	 * @param recordPrice
	 * @param defContent
	 * @param recordQty
	 * @param recordPrdContent
	 * @param bonusRedAmount
	 * @return
	 */
	public Double priceByBonusTotalPerIncludingExcludingPromo(Double recordPrice, Double defContent, Double recordQty, Double recordPrdContent, Double bonusRedAmount) {
		
		return ( recordPrice * ( 1 - (bonusRedAmount/ 100) ) * defContent) /  (recordQty * recordPrdContent);
	}
	
	
	/**
	 * @param recordPrice
	 * @param defContent
	 * @param recordQty
	 * @param recordPrdContent
	 * @param bonusRedAmount
	 * @return
	 */
	public Double priceByBonusPiecePerZero(Double recordPrice, Double defContent, Double recordQty, Double recordPrdContent, Double bonusRedAmount) {
		return recordPrice;
	}
	
	/**
	 * @param recordPrice
	 * @param defContent
	 * @param recordQty
	 * @param recordPrdContent
	 * @param bonusRedAmount
	 * @return
	 */
	public Double priceByBonusTotalPerZero(Double recordPrice, Double defContent, Double recordQty, Double recordPrdContent, Double bonusRedAmount) {
		
		Double unitPrice = recordPrice; 
		return unitPrice;
	}
	
	public static double round(double value, int places) {
	    if (places < 0) throw new IllegalArgumentException();

	    BigDecimal bd = new BigDecimal(value);
	    bd = bd.setScale(places, RoundingMode.HALF_UP);
	    return bd.doubleValue();
	}
}
