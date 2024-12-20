# act_ai/views.py
from rest_framework import serializers
from rest_framework.views import APIView
from rest_framework.response import Response
from rest_framework_simplejwt.authentication import JWTAuthentication
from rest_framework import status, permissions
from ai_module.AI_API import AiAPI
from ai_module.backend_client import BackendClient
import logging

# Configuring logging
logger = logging.getLogger(__name__)

# Serialiser for PredictView
class PredictSerializer(serializers.Serializer):
    symbol = serializers.CharField(required=True, max_length=10)

class PredictView(APIView):
    """
    POST /api/act-ai/predict/
    Generates a prediction for the specified character.
    """
    authentication_classes = [JWTAuthentication]
    permission_classes = [permissions.IsAuthenticated]

    def post(self, request):
        try:
            # Input data validation
            serializer = PredictSerializer(data=request.data)
            serializer.is_valid(raise_exception=True)

            # Data extraction
            symbol = serializer.validated_data['symbol']

            # Using AI_API for prediction
            ai = AiAPI()
            forecast = ai.get_forecast(forecast_id="1", symbol=symbol, user_id=request.user.id)

            return Response(forecast, status=status.HTTP_200_OK)

        except Exception as e:
            # Error Logging
            logger.error(f"Error in PredictView: {e}", exc_info=True)
            return Response({"error": str(e)}, status=status.HTTP_500_INTERNAL_SERVER_ERROR)


# Serialiser for HistoryView
class HistorySerializer(serializers.Serializer):
    symbol = serializers.CharField(required=True, max_length=10)

class HistoryView(APIView):
    """
    GET /api/act-ai/history/
    Returns the history for the specified character.
    """
    authentication_classes = [JWTAuthentication]
    permission_classes = [permissions.IsAuthenticated]

    def get(self, request):
        try:
            # Retrieving query parameters
            symbol = request.query_params.get('symbol')
            if not symbol:
                return Response({"error": "Symbol is required"}, status=status.HTTP_400_BAD_REQUEST)

            # Retrieving history via BackendClient
            client = BackendClient()
            history = client.get_historical_data(symbol=symbol)

            if history:
                return Response(history, status=status.HTTP_200_OK)
            return Response({"message": "No history found"}, status=status.HTTP_404_NOT_FOUND)

        except Exception as e:
            # Error Logging
            logger.error(f"Error in HistoryView: {e}", exc_info=True)
            return Response({"error": str(e)}, status=status.HTTP_500_INTERNAL_SERVER_ERROR)


class TradeRatingView(APIView):
    """
    API endpoint to get the trade rating for a given symbol.
    """
    authentication_classes = [JWTAuthentication]
    permission_classes = [permissions.IsAuthenticated]

    def get(self, request):
        try:
            # Retrieve the symbol from query parameters
            symbol = request.query_params.get('symbol')
            if not symbol:
                return Response({"error": "Symbol is required"}, status=status.HTTP_400_BAD_REQUEST)

            # Initialize AI_API and fetch trade rating
            ai_api = AiAPI()
            trade_rating = ai_api.get_trade_rating(symbol=symbol, user_id=request.user.id)

            # Return the trade rating if available
            if trade_rating:
                return Response({"trade_rating": trade_rating}, status=status.HTTP_200_OK)

            # If no trade rating is found
            return Response({"message": "No trade rating found"}, status=status.HTTP_404_NOT_FOUND)

        except Exception as e:
            # Log any exceptions and return an error response
            logger.error(f"Error in TradeRatingView: {e}", exc_info=True)
            return Response({"error": str(e)}, status=status.HTTP_500_INTERNAL_SERVER_ERROR)

class FinnhubStockDataView(APIView):
    """
    GET /api/act-ai/stock-data/
    Fetch stock market data for a given symbol using Finnhub API.
    """
    authentication_classes = [JWTAuthentication]
    permission_classes = [permissions.IsAuthenticated]

    def get(self, request):
        symbol = request.query_params.get('symbol')
        if not symbol:
            return Response({"error": "Symbol is required"}, status=status.HTTP_400_BAD_REQUEST)

        ai_api = AiAPI()
        try:
            stock_data = ai_api.get_finnhub_stock_data(symbol)
            return Response(stock_data, status=status.HTTP_200_OK)
        except Exception as e:
            return Response({"error": str(e)}, status=status.HTTP_500_INTERNAL_SERVER_ERROR)


class FinnhubNewsView(APIView):
    """
    GET /api/act-ai/stock-news/
    Fetch news for a given category using Finnhub API.
    """
    authentication_classes = [JWTAuthentication]
    permission_classes = [permissions.IsAuthenticated]

    def get(self, request):
        category = request.query_params.get('category', 'general')
        
        ai_api = AiAPI()
        try:
            news = ai_api.get_finnhub_news(category)
            return Response(news, status=status.HTTP_200_OK)
        except Exception as e:
            return Response({"error": str(e)}, status=status.HTTP_500_INTERNAL_SERVER_ERROR)


class CoinDataView(APIView):
    """
    GET /api/act-ai/coin-data/
    Fetch cryptocurrency data for a given coin ID using CoinGecko API.
    """
    authentication_classes = [JWTAuthentication]
    permission_classes = [permissions.IsAuthenticated]

    def get(self, request):
        coin_id = request.query_params.get('coin_id', 'bitcoin')
        
        ai_api = AiAPI()
        try:
            coin_data = ai_api.get_coingecko_coin_data(coin_id)
            return Response(coin_data, status=status.HTTP_200_OK)
        except Exception as e:
            return Response({"error": str(e)}, status=status.HTTP_500_INTERNAL_SERVER_ERROR)


class TrendingCoinsView(APIView):
    """
    GET /api/act-ai/trending-coins/
    Fetch trending cryptocurrencies using CoinGecko API.
    """
    authentication_classes = [JWTAuthentication]
    permission_classes = [permissions.IsAuthenticated]

    def get(self, request):
        ai_api = AiAPI()
        try:
            trending_coins = ai_api.get_coingecko_trending_coins()
            return Response(trending_coins, status=status.HTTP_200_OK)
        except Exception as e:
            return Response({"error": str(e)}, status=status.HTTP_500_INTERNAL_SERVER_ERROR)