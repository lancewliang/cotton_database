//+------------------------------------------------------------------+
//|                                                    stringlib.mqh |
//|                                       Copyright Bogdan Caramalac |
//|                                           http://mqlmagazine.com |
//+------------------------------------------------------------------+
#property library 
#property copyright "Bogdan Caramalac"
#property link      "http://mqlmagazine.com"
#property version   "1.00"
 
string ANSI2UNICODE(string s)
  {
   ushort mychar;
   long m,d;
   double mm,dd;
   string img;    
   string res="";
   if (StringLen(s)>0)
     {
      string g=" ";
      for (int i=0;i<StringLen(s);i++)
         {          
          string f="  ";          
          mychar=ushort(StringGetCharacter(s,i));
          mm=MathMod(mychar,256);
          img=DoubleToString(mm,0);
          m=StringToInteger(img);
          dd=(mychar-m)/256;
          img=DoubleToString(dd,0);
          d=StringToInteger(img);
          if (m!=0)
            {
             StringSetCharacter(f,0,ushort(m));
             StringSetCharacter(f,1,ushort(d));
             res = StringConcatenate(res,f);
            }//if (m!=0)
          else
            break;                      
         }//for (int i=0;i<StringLen(s);i++)
      }//if (StringLen(s)>0)
   return(res);
  }
 
string UNICODE2ANSI(string s)
  {
   int leng,ipos;
   uchar m,d;
   ulong big;
   leng=StringLen(s);
   string unichar;
   string res="";
   if (leng!=0)
     {    
      unichar=" ";
      ipos=0;      
      while (ipos<leng)
        { //uchar typecasted because each double byte char is actually one byte
         m=uchar(StringGetCharacter(s,ipos));
         if (ipos+1<leng)
           d=uchar(StringGetCharacter(s,ipos+1));
         else
           d=0;
         big=d*256+m;                
         StringSetCharacter(unichar,0,ushort(big));         
         res = StringConcatenate(res,unichar);    
         ipos=ipos+2;
        }
     }
   return(res);
  }