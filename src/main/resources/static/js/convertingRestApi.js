function inputChangedAndConvertOneElement(sumOne) {
    let firstValueElement = 0;
    let secondValueElement = 0;

    //getting second element's id
    let selindFirst = document.getElementById("checkbox1").options.selectedIndex;
    let idFirst = document.getElementById("checkbox1").options[selindFirst].value;

    //getting first element
    $.ajax({
        url: '/api/currency/getCurrencyCost/' + idFirst,
        type: 'GET',
        dataType: 'json',
        contentType: 'application/json',
        async: false,
        success: function (currency) {
            firstValueElement = currency.value * currency.nominal * sumOne;
        }
    })

    //getting second element's id
    let selindSecond = document.getElementById("checkbox2").options.selectedIndex;
    let idSecond = document.getElementById("checkbox2").options[selindSecond].value;

    //getting second element
    $.ajax({
        url: '/api/currency/getCurrencyCost/' + idSecond,
        type: 'GET',
        dataType: 'json',
        contentType: 'application/json',
        async: false,
        success: function (currency) {
            secondValueElement = (1 / currency.value) * currency.nominal;
        }
    })

    let result = firstValueElement * secondValueElement;

    $('#targetValueCurrency').val(result.toFixed(2));
}

function inputChangedAndConvertTwoElement(sumTwo) {
    let firstValueElement = 0;
    let secondValueElement = 0;

    //getting first element's id
    let selindFirst = document.getElementById("checkbox1").options.selectedIndex;
    let idFirst = document.getElementById("checkbox1").options[selindFirst].value;

    //getting first element
    $.ajax({
        url: '/api/currency/getCurrencyCost/' + idFirst,
        type: 'GET',
        dataType: 'json',
        contentType: 'application/json',
        async: false,
        success: function (currency) {
            firstValueElement = (1 / currency.value) * currency.nominal;
        }
    })

    //getting second element's id
    let selindSecond = document.getElementById("checkbox2").options.selectedIndex;
    let idSecond = document.getElementById("checkbox2").options[selindSecond].value;

    //getting second element
    $.ajax({
        url: '/api/currency/getCurrencyCost/' + idSecond,
        type: 'GET',
        dataType: 'json',
        contentType: 'application/json',
        async: false,
        success: function (currency) {
            secondValueElement = currency.value * currency.nominal * sumTwo;

        }
    })
    let result = firstValueElement * secondValueElement;

    $('#initialValueCurrency').val(result.toFixed(2));
}

function selectChangedAndConvertTwoElement(idTarget) {
    //getting first element's id
    let selind = document.getElementById("checkbox1").options.selectedIndex;
    let id = document.getElementById("checkbox1").options[selind].value;

    let sum = $('#initialValueCurrency').val();
    let firstValueElement = 0;
    let secondValueElement = 0;
    //getting first element
    $.ajax({
        url: '/api/currency/getCurrencyCost/' + id,
        type: 'GET',
        dataType: 'json',
        contentType: 'application/json',
        async: false,
        success: function (currency) {
            firstValueElement = currency.value * currency.nominal * sum;
        }
    })

    //getting second element
    $.ajax({
        url: '/api/currency/getCurrencyCost/' + idTarget,
        type: 'GET',
        dataType: 'json',
        contentType: 'application/json',
        async: false,
        success: function (currency) {
            secondValueElement = (1 / currency.value) * currency.nominal;
        }
    })
    let result = firstValueElement * secondValueElement;

    $('#targetValueCurrency').val(result.toFixed(2));
}

function selectChangedAndConvertOneElement(idTarget) {
    let sum = $('#initialValueCurrency').val();
    let firstValueElement = 0;
    let secondValueElement = 0;

    //getting first element
    $.ajax({
        url: '/api/currency/getCurrencyCost/' + idTarget,
        type: 'GET',
        dataType: 'json',
        contentType: 'application/json',
        async: false,
        success: function (currency) {
            firstValueElement = currency.value * currency.nominal * sum;
        }
    })
    //getting second element's id
    let selind = document.getElementById("checkbox2").options.selectedIndex;
    let id = document.getElementById("checkbox2").options[selind].value;

    //getting second element
    $.ajax({
        url: '/api/currency/getCurrencyCost/' + id,
        type: 'GET',
        dataType: 'json',
        contentType: 'application/json',
        async: false,
        success: function (currency) {
            secondValueElement = (1 / currency.value) * currency.nominal;
        }
    })
    let result = firstValueElement * secondValueElement;

    $('#targetValueCurrency').val(result.toFixed(2));
}