Feature: Online Process

  @deneme
  Scenario: Google Testinium Search
    * "https://account.testinium.com/uaa/login" sayfasina git
    * "txtInput" elementine "testinium" degerini yaz
    * "txtInput" elementine "ENTER" key gonder

  @deneme01
  Scenario: deneme01
    * "https://www.amazon.com.tr/" sayfasina git
    * 2 saniye bekle
    * "logoerror" elementine tikla
    * 2 saniye bekle
    * "search" elementine tikla


  @deneme02
  Scenario: deneme02
    * "https://www.amazon.com.tr/" sayfasina git
    * 2 saniye bekle
    * "searchbarerror" elementine tikla
    * 2 saniye bekle
    * "searchbar" elementine "telefon" degerini yaz
    * 2 saniye bekle
