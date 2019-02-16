<html>
    <head>
        <#import "../helpers/header.ftl" as header>
        <@header.header "BudgetMaster"/>
        <@header.style "filter"/>
        <#import "/spring.ftl" as s>
    </head>
    <body class="budgetmaster-blue-light">
        <#import "../helpers/navbar.ftl" as navbar>
        <@navbar.navbar ""/>

        <main>
            <div class="card main-card background-color">
                <div class="container">
                    <form name="NewFilterConfiguration" action="<@s.url '/filter/apply'/>" method="post">
                        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>

                        <div class="row">
                            <div class="s12">
                                <ul class="collapsible" id="filterSettings">
                                    <li id="section-type">
                                        <div class="collapsible-header">
                                            <i class="fas fa-piggy-bank"></i>
                                            ${locale.getString("filter.type")}
                                            <div class="collapsible-header-status">1/2</div>
                                        </div>
                                        <div class="collapsible-body">
                                            <div class="row no-margin">
                                                <div class="col s6 m6 l6">
                                                    <label>
                                                        <input type="checkbox" name="includeIncome" <#if filterConfiguration.isIncludeIncome()>checked="checked"</#if>>
                                                        <span class="text-color">${locale.getString('filter.type.income')}</span>
                                                    </label>
                                                </div>
                                                <div class="col s6 m6 l6">
                                                    <label>
                                                        <input type="checkbox" name="includeExpenditure" <#if filterConfiguration.isIncludeExpenditure()>checked="checked"</#if>>
                                                        <span class="text-color">${locale.getString('filter.type.expenditure')}</span>
                                                    </label>
                                                </div>
                                            </div>
                                        </div>
                                    </li>

                                    <li id="section-repeating">
                                        <div class="collapsible-header">
                                            <i class="material-icons">repeat</i>
                                            ${locale.getString("filter.repeating")}
                                            <div class="collapsible-header-status">1/2</div>
                                        </div>
                                        <div class="collapsible-body">
                                            <div class="row no-margin">
                                                <div class="col s6 m6 l6">
                                                    <label>
                                                        <input type="checkbox" name="includeNotRepeating" <#if filterConfiguration.isIncludeNotRepeating()>checked="checked"</#if>>
                                                        <span class="text-color">${locale.getString('filter.repeating.false')}</span>
                                                    </label>
                                                </div>
                                                <div class="col s6 m6 l6">
                                                    <label>
                                                        <input type="checkbox" name="includeRepeating" <#if filterConfiguration.isIncludeRepeating()>checked="checked"</#if>>
                                                        <span class="text-color">${locale.getString('filter.repeating.true')}</span>
                                                    </label>
                                                </div>
                                            </div>
                                        </div>
                                    </li>

                                    <li id="section-categories">
                                        <div class="collapsible-header">
                                            <i class="material-icons">label</i>
                                            ${locale.getString("filter.categories")}
                                            <div class="collapsible-header-status">1/2</div>
                                        </div>
                                        <div class="collapsible-body">
                                            <div class="row no-margin">
                                                <div class="col s12">
                                                    <#list filterConfiguration.getFilterCategories() as filterCategory>
                                                        <div>
                                                            <label>
                                                                <input type="checkbox" name="filterCategories['${filterCategory?index}'].include">
                                                                <span class="text-color">${filterCategory.getName()}</span>
                                                            </label>
                                                            <input type="hidden" name="filterCategories['${filterCategory?index}'].ID" value="${filterCategory.getID()}"/>
                                                            <input type="hidden" name="filterCategories['${filterCategory?index}'].name" value="${filterCategory.getName()}"/>
                                                        </div>
                                                    </#list>
                                                </div>
                                            </div>
                                        </div>
                                    </li>
                                </ul>
                            </div>
                        </div>

                        <#-- button save -->
                        <div class="row valign-wrapper">
                            <div class="col s12 center-align">
                                <button class="btn waves-effect waves-light budgetmaster-blue" type="submit" name="buttonSave">
                                    <i class="fas fa-filter left"></i>${locale.getString("filter.apply")}
                                </button>
                            </div>
                        </div>
                    </form>
                </div>
            </div>
        </main>

        <!--  Scripts-->
        <#import "../helpers/scripts.ftl" as scripts>
        <@scripts.scripts/>
        <script src="<@s.url '/js/filter.js'/>"></script>
    </body>
</html>