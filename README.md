# 練習用的專案
~~先簡單設個目標，不然我怕會越研究越深最後什麼都做不出來XD~~  
主要是想練習SpringSecurity+JWT做用戶登入和權限驗證  
權限設計用RBAC(Role-Base-Access-Control)的方式  
i18n  
Websocket做即時系統通知，沒有區分服務所以沒用kafka  
Quartz做排程(月結薪資，每日打卡紀錄之類的)  
jxls做報表  
javaMail  
Redis緩存  
前端Vus.js  
資料庫SpringDataJpa + MSSQL  
~~順便練習寫TDD和測試(測試最後再補，花太多時間了)  
剩下有想到再補~~

SpringSecurity筆記:https://hackmd.io/@S1MNxjTORYSfAfivxMlEQw/rykdZBNEa

***
## 2024.1.2
舊設計棄置(branch master_abandoned)  
主要使用技術不變  
權限都改為多對多  
i18n僅保留中/英  
目標比較明確了，主結構會按照下列去完成  
![img.png](src/main/resources/template/file/img.png)  
整體內容大致都想好怎麼做了  
就是剩下權限控管的部分，因為要區分前後端  
不太確定到底該怎麼切  
(  
目前做法是每支api有自己的權限可以設定對應角色，  
前端部分是延用route的概念，希望是能做到區分頁面顯示與否  
有想法會再改  
)  

目前緩存設計也不太成熟  
目前是利用區分cacheName來切分不同區塊緩存刷新  
但緩存內資料複雜，又有多方關聯(像是client對多個entity都是ManyToMany之類的)  
只刷單一區塊會變成刷不乾淨，全刷又太浪費  
再想辦法處理  
目前待處理有  
(  
1.view-Router設計實現、  
2.緩存寫unitTest?主要是只想針對緩存做測試，不想為了cache test完整啟動springboot上下文、    
3.配置類寫測試的必要性?  
)