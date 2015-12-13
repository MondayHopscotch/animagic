#ifdef GL_ES
precision mediump float;
#endif
varying vec4 v_color;
varying vec2 v_texCoords;
uniform sampler2D u_texture;
uniform sampler2D u_normals;
uniform vec3 light0;
uniform vec3 light1;
uniform vec3 light2;
uniform vec3 light3;
uniform vec3 light4;
uniform vec3 light5;
uniform vec3 light6;
uniform vec3 light7;
uniform vec3 light8;
uniform vec3 light9;
uniform vec3 lightColor0;
uniform vec3 lightColor1;
uniform vec3 lightColor2;
uniform vec3 lightColor3;
uniform vec3 lightColor4;
uniform vec3 lightColor5;
uniform vec3 lightColor6;
uniform vec3 lightColor7;
uniform vec3 lightColor8;
uniform vec3 lightColor9;
uniform float attenuation0;
uniform float attenuation1;
uniform float attenuation2;
uniform float attenuation3;
uniform float attenuation4;
uniform float attenuation5;
uniform float attenuation6;
uniform float attenuation7;
uniform float attenuation8;
uniform float attenuation9;
uniform float ambientIntensity;
uniform vec3 ambientColor;
uniform vec2 resolution;
uniform bool useNormals;
uniform bool useShadow;
uniform float strength;
uniform bool yInvert;

uniform float xCoordMin;
uniform float xCoordDiff;
uniform float yCoordMin;
uniform float yCoordDiff;
uniform float xNorCoordMin;
uniform float xNorCoordDiff;
uniform float yNorCoordMin;
uniform float yNorCoordDiff;

vec3 calculateLight(vec2 texCoord,
                    vec3 light,
                    vec3 normal,
                    vec2 resolution,
                    float attenuation,
                    bool useShadow,
                    vec3 lightColor,
                    vec4 vertexColor,
                    vec4 textureColor);

void main() {
    vec2 norCoords = vec2(xNorCoordMin + (((v_texCoords.x - xCoordMin) / xCoordDiff) * xNorCoordDiff),
                          yNorCoordMin + (((v_texCoords.y - yCoordMin) / yCoordDiff) * yNorCoordDiff));
    vec4 color = texture2D(u_texture, v_texCoords);
    vec3 nColor = texture2D(u_normals, norCoords).rgb;
    //some bump map programs will need the Y value flipped..
    nColor.g = yInvert ? 1.0 - nColor.g : nColor.g;
    //this is for debugging purposes, allowing us to lower the intensity of our bump map
    vec3 nBase = vec3(1.0, 1.0, 1.0);
    nColor = mix(nBase, nColor, strength);
    //normals need to be converted to [-1.0, 1.0] range and normalized
    vec3 normal = normalize(nColor * 2.0 - 1.0);
    vec3 ambient = vec3(ambientColor * ambientIntensity);
    if (useNormals){
        vec3 additiveBlending =
            ambient +
            calculateLight(v_texCoords.xy, light0, normal, resolution, attenuation0, useShadow, lightColor0, v_color, color) +
            calculateLight(v_texCoords.xy, light1, normal, resolution, attenuation1, useShadow, lightColor1, v_color, color) +
            calculateLight(v_texCoords.xy, light2, normal, resolution, attenuation2, useShadow, lightColor2, v_color, color) +
            calculateLight(v_texCoords.xy, light3, normal, resolution, attenuation3, useShadow, lightColor3, v_color, color) +
            calculateLight(v_texCoords.xy, light4, normal, resolution, attenuation4, useShadow, lightColor4, v_color, color) +
            calculateLight(v_texCoords.xy, light5, normal, resolution, attenuation5, useShadow, lightColor5, v_color, color) +
            calculateLight(v_texCoords.xy, light6, normal, resolution, attenuation6, useShadow, lightColor6, v_color, color) +
            calculateLight(v_texCoords.xy, light7, normal, resolution, attenuation7, useShadow, lightColor7, v_color, color) +
            calculateLight(v_texCoords.xy, light8, normal, resolution, attenuation8, useShadow, lightColor8, v_color, color) +
            calculateLight(v_texCoords.xy, light9, normal, resolution, attenuation9, useShadow, lightColor9, v_color, color);
        gl_FragColor = vec4(additiveBlending, color.a);
    } else {
        gl_FragColor = color;
    }
}

vec3 calculateLight(vec2 texCoord,
                    vec3 light,
                    vec3 normal,
                    vec2 resolution,
                    float attenuation,
                    bool useShadow,
                    vec3 lightColor,
                    vec4 vertexColor,
                    vec4 textureColor) {
    vec3 deltaPos = vec3( (light.xy - texCoord) / resolution.xy, light.z );

    vec3 lightDir = normalize(deltaPos);
    float lambert = useNormals ? clamp(dot(normal, lightDir), 0.0, 1.0) : 1.0;

    float d = sqrt(dot(deltaPos, deltaPos));
    float att = useShadow ? 1.0 / ( attenuation + (attenuation*d) + (attenuation*d*d) ) : 1.0;

    vec3 result = (lightColor.rgb * lambert) * att;
    result *= textureColor.rgb;
    result *= vertexColor.rgb;
    return result;
}