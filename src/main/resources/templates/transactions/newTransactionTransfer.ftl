<html>
    <head>
        <#import "../helpers/header.ftl" as header>
        <@header.header "BudgetMaster"/>
        <@header.style "transactions"/>
        <@header.style "datepicker"/>
        <@header.style "categories"/>
        <@header.style "collapsible"/>
        <#import "/spring.ftl" as s>
    </head>
    <body class="budgetmaster-blue-light">
        <#import "../helpers/navbar.ftl" as navbar>
        <@navbar.navbar "transactions" settings/>

        <#import "newTransactionMacros.ftl" as newTransactionMacros>

        <main>
            <div class="card main-card background-color">
                <div class="container">
                    <div class="section center-align">
                        <#assign title = locale.getString("title.transaction.new.transfer")/>
                        <div class="headline"><#if isEdit>${locale.getString("title.transaction.edit", title)}<#else>${locale.getString("title.transaction.new", title)}</#if></div>
                    </div>
                </div>
                <div class="container">
                    <#import "../helpers/validation.ftl" as validation>
                    <form name="NewTransaction" action="<@s.url '/transactions/newTransaction/transfer'/>" method="post" onsubmit="return validateForm()">
                        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                        <!-- only set ID for transactions not templates, otherwise the input is filled with the template ID and saving the transaction
                        may then override an existing transactions if the ID is also already used in transactions table -->
                        <input type="hidden" name="ID" value="<#if transaction.class.simpleName == "Transaction" && transaction.getID()??>${transaction.getID()?c}</#if>">
                        <input type="hidden" name="isExpenditure" value="true">

                        <#-- name -->
                        <@newTransactionMacros.transactionName transaction suggestionsJSON/>

                        <#-- amount -->
                        <@newTransactionMacros.transactionAmount transaction/>

                        <#-- category -->
                        <@newTransactionMacros.categorySelect categories transaction.getCategory() "col s12 m12 l8 offset-l2" locale.getString("transaction.new.label.category")/>

                        <#-- date -->
                        <@newTransactionMacros.transactionStartDate transaction currentDate/>

                        <#-- description -->
                        <@newTransactionMacros.transactionDescription transaction/>

                        <#-- tags -->
                        <@newTransactionMacros.transactionTags transaction/>

                        <#-- account -->
                        <#if transaction.getAccount()??>
                            <@newTransactionMacros.account accounts transaction.getAccount() "transaction-account" "account" locale.getString("transaction.new.label.account"), false/>
                        <#else>
                            <@newTransactionMacros.account accounts helpers.getCurrentAccountOrDefault() "transaction-account" "account" locale.getString("transaction.new.label.account") false/>
                        </#if>

                        <#-- transfer account -->
                        <#if transaction.getTransferAccount()??>
                            <@newTransactionMacros.account accounts transaction.getTransferAccount() "transaction-transfer-account" "transferAccount" locale.getString("transaction.new.label.transfer.account") false/>
                        <#else>
                            <@newTransactionMacros.account accounts helpers.getCurrentAccountOrDefault() "transaction-transfer-account" "transferAccount" locale.getString("transaction.new.label.transfer.account") false/>
                        </#if>

                        <br>
                        <#-- buttons -->
                        <@newTransactionMacros.buttons "/transactions"/>
                        <@newTransactionMacros.buttonTemplate/>
                    </form>

                    <div id="saveAsTemplateModalContainer"></div>
                </div>
            </div>
        </main>

        <!-- Pass localization to JS -->
        <#import "../helpers/globalDatePicker.ftl" as datePicker>
        <@datePicker.datePickerLocalization/>

        <script>
            createTemplateWithErrorInForm = '${locale.getString("save.as.template.errorsInForm")}';
            templateNameEmptyValidationMessage = "${locale.getString("warning.empty.transaction.name")}";
            templateNameDuplicateValidationMessage = "${locale.getString("warning.duplicate.template.name")}";
        </script>

        <!-- Scripts-->
        <#import "../helpers/scripts.ftl" as scripts>
        <@scripts.scripts/>
        <script src="<@s.url '/js/libs/spectrum.js'/>"></script>
        <script src="<@s.url '/js/helpers.js'/>"></script>
        <script src="<@s.url '/js/transactions.js'/>"></script>
        <script src="<@s.url '/js/categorySelect.js'/>"></script>
        <script src="<@s.url '/js/templates.js'/>"></script>
    </body>
</html>
