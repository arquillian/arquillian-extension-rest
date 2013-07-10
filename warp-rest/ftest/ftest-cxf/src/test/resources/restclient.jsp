<%@page pageEncoding="UTF-8" contentType="text/html; ISO-8859-1" %>

<!DOCTYPE html>
<html>
<head>
    <title>Ajax JAX-RS call</title>

    <script type="text/javascript" src="js/jquery-1.8.2.min.js"></script>

    <script type="text/javascript">
        function onStockClick() {

            var stockId = $(this).data('stockId');
            var stock = $.get('app/rest/stocks/' + stockId);

            // TODO do something with the stock
        }

        function displayStocks(data) {

            var table = $('<table />').append($('<thead />')
                    .append($('<tr />')
                            .append($('<td />').text('Name'))
                            .append($('<td />').text('Code'))
                            .append($('<td />').text('Value'))
                    )
            );

            $.each(data, function (ind, val) {

                var row = $('<tr />');
                $('<td />').append($('<a />', {class: 'stockLink'}).text(val.name).attr('href', '#').data('stockId', val.id).click(onStockClick)).appendTo(row);
                $('<td />').text(val.code).appendTo(row);
                $('<td />').text(val.value).appendTo(row);

                table.append(row);
            });

            table.appendTo('#mainContent');
        }

        function getStocks(callback) {

            if (typeof callback != 'undefined') {
                $.get('app/rest/stocks', callback);
            } else {

                throw {
                    message: "The callback has not been specified",
                    name: "Argument error"
                };
            }
        }

        $('document').ready(function () {

            getStocks(displayStocks);
        });

    </script>
</head>

<body>
<!-- page body -->
<div id="mainContent"/>
</body>

</html>