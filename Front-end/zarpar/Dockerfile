FROM node:latest AS build
WORKDIR /src
RUN npm install -g @angular/cli
COPY . ./
RUN npm ci
RUN ng build --configuration=production

FROM nginx:alpine
COPY --from=build /src/dist/zarpar/browser /usr/share/nginx/html
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
