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

***
## 2024.3.19
後端部分API大致完成  
總結對SpringSecurity+JWT的實現以及理解  
先實現<font color="#f00">UserDetails</font>，用於驗證該用戶狀態及權限  
先實現<font color="#f00">UserDetailsService</font>，用於登入驗證時使用userName找到該用戶，返回UserDetails實現類  
把先前實現的UserDetailsService類注入<font color="#f00">AuthenticationProvider</font>(DaoAuthenticationProvider)  
<font color="#f00">SecurityFilterChain</font>設置.addFilterBefore(<font color="#f00">JwtFilter</font>, UsernamePasswordAuthenticationFilter.class)
之後登入就會先進行JWT驗證才會走到security的權限驗證  
這邊的API權限設置也都是用動態加仔的方式設置在SecurityFilterChain  
後續流程:  
用戶註冊時，密碼經由指定的<font color="#f00">PasswordEncoder</font>(像這邊是使用BCryptPasswordEncoder)  
加密後存入資料庫  
後續用戶發起登入請求，把帳密放進UsernamePasswordAuthenticationToken  
進行.authenticate(會使用先前實現的UserDetailsService類進行驗證)  
驗證帳號密碼是否通過和驗證該用戶相關狀態  
如果通過就產出並核發JWT返回  
(如果有勾選rememberMe才會返回refreshToken，否則就是單依靠accessToken做後續操作)  
並且因為SecurityContext只保留再請求的生命週期間  
之後登入成功後的每個請求流程都是:  
Filter先驗證AccessToken是否過期，  
如果過期就再驗證RefreshToken是否過期和是否在黑名單內  
(如果登入沒有勾選rememberMe，這邊就會直接拋出要求重登)  
如果通過驗證則把AccessToken和RefreshToken都刷新，並且把舊Token加入Redis黑名單  
如果RefreshToken也過期，就直接拋出，返回Unauthorized  
驗證過程中只要是過期以外的錯誤都是直接返回403  
並且只要沒有拋出(JWT驗證通過)  
就必須透過JWT內儲存的用戶資訊產出Authentication以利該請求進行後續權限驗證  
(目前是只存用戶名稱，想秉持用戶驗證和權限驗證的獨立性但不確定優劣)  
然後因為是每次用戶請求都會產生新的Authentication，所以權限是即時更新的
但缺點就是每次請求都要查詢當下權限，目前是只想到利用緩存優化
另外  
這邊JWT設計是用非對稱式加密  
公鑰給前端，前後都需要驗證JWT時效和簽名  