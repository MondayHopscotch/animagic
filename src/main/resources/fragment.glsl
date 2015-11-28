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
uniform float intensity0;
uniform float intensity1;
uniform float intensity2;
uniform float intensity3;
uniform float intensity4;
uniform float intensity5;
uniform float intensity6;
uniform float intensity7;
uniform float intensity8;
uniform float intensity9;
uniform vec3 attenuation0;
uniform vec3 attenuation1;
uniform vec3 attenuation2;
uniform vec3 attenuation3;
uniform vec3 attenuation4;
uniform vec3 attenuation5;
uniform vec3 attenuation6;
uniform vec3 attenuation7;
uniform vec3 attenuation8;
uniform vec3 attenuation9;
uniform vec3 ambientColor;
uniform vec2 resolution;
uniform bool useNormals;
uniform bool useShadow;
uniform float strength;
uniform bool yInvert;

void main() {
    //sample color & normals from our textures
    vec4 color = texture2D(u_texture, v_texCoords.st);
    vec3 nColor = texture2D(u_normals, v_texCoords.st).rgb;

    //some bump map programs will need the Y value flipped..
    nColor.g = yInvert ? 1.0 - nColor.g : nColor.g;

    //this is for debugging purposes, allowing us to lower the intensity of our bump map
    vec3 nBase = vec3(0.5, 0.5, 1.0);
    nColor = mix(nBase, nColor, strength);

    //normals need to be converted to [-1.0, 1.0] range and normalized
    vec3 normal = normalize(nColor * 2.0 - 1.0);

    // ///////////////////////////////////////////////////
    vec3 deltaPos = vec3( (light0.xy - gl_FragCoord.xy) / resolution.xy, light0.z );

    vec3 lightDir = normalize(deltaPos);
    float lambert = useNormals ? clamp(dot(normal, lightDir), 0.0, 1.0) : 1.0;

    float d = sqrt(dot(deltaPos, deltaPos));
    float att = useShadow ? 1.0 / ( attenuation0.x + (attenuation0.y*d) + (attenuation0.z*d*d) ) : 1.0;

    vec3 result = (ambientColor * intensity0) + (lightColor0.rgb * lambert) * att;
    result *= color.rgb;
    vec4 resultA = v_color * vec4(result, color.a);
    // ///////////////////////////////////////////////////
    deltaPos = vec3( (light1.xy - gl_FragCoord.xy) / resolution.xy, light1.z );

    lightDir = normalize(deltaPos);
    lambert = useNormals ? clamp(dot(normal, lightDir), 0.0, 1.0) : 1.0;

    d = sqrt(dot(deltaPos, deltaPos));
    att = useShadow ? 1.0 / ( attenuation1.x + (attenuation1.y*d) + (attenuation1.z*d*d) ) : 1.0;

    result = (ambientColor * intensity1) + (lightColor1.rgb * lambert) * att;
    result *= color.rgb;
    vec4 resultB = v_color * vec4(result, color.a);
    // ///////////////////////////////////////////////////
    deltaPos = vec3( (light2.xy - gl_FragCoord.xy) / resolution.xy, light2.z );

    lightDir = normalize(deltaPos);
    lambert = useNormals ? clamp(dot(normal, lightDir), 0.0, 1.0) : 1.0;

    d = sqrt(dot(deltaPos, deltaPos));
    att = useShadow ? 1.0 / ( attenuation2.x + (attenuation2.y*d) + (attenuation2.z*d*d) ) : 1.0;

    result = (ambientColor * intensity2) + (lightColor2.rgb * lambert) * att;
    result *= color.rgb;
    vec4 resultC = v_color * vec4(result, color.a);
    // ///////////////////////////////////////////////////
    deltaPos = vec3( (light3.xy - gl_FragCoord.xy) / resolution.xy, light3.z );

    lightDir = normalize(deltaPos);
    lambert = useNormals ? clamp(dot(normal, lightDir), 0.0, 1.0) : 1.0;

    d = sqrt(dot(deltaPos, deltaPos));
    att = useShadow ? 1.0 / ( attenuation3.x + (attenuation3.y*d) + (attenuation3.z*d*d) ) : 1.0;

    result = (ambientColor * intensity3) + (lightColor3.rgb * lambert) * att;
    result *= color.rgb;
    vec4 resultD = v_color * vec4(result, color.a);
    // ///////////////////////////////////////////////////
    deltaPos = vec3( (light4.xy - gl_FragCoord.xy) / resolution.xy, light4.z );

    lightDir = normalize(deltaPos);
    lambert = useNormals ? clamp(dot(normal, lightDir), 0.0, 1.0) : 1.0;

    d = sqrt(dot(deltaPos, deltaPos));
    att = useShadow ? 1.0 / ( attenuation4.x + (attenuation4.y*d) + (attenuation4.z*d*d) ) : 1.0;

    result = (ambientColor * intensity4) + (lightColor4.rgb * lambert) * att;
    result *= color.rgb;
    vec4 resultE = v_color * vec4(result, color.a);
    // ///////////////////////////////////////////////////
    deltaPos = vec3( (light5.xy - gl_FragCoord.xy) / resolution.xy, light5.z );

    lightDir = normalize(deltaPos);
    lambert = useNormals ? clamp(dot(normal, lightDir), 0.0, 1.0) : 1.0;

    d = sqrt(dot(deltaPos, deltaPos));
    att = useShadow ? 1.0 / ( attenuation5.x + (attenuation5.y*d) + (attenuation5.z*d*d) ) : 1.0;

    result = (ambientColor * intensity5) + (lightColor5.rgb * lambert) * att;
    result *= color.rgb;
    vec4 resultF = v_color * vec4(result, color.a);
    // ///////////////////////////////////////////////////
    deltaPos = vec3( (light6.xy - gl_FragCoord.xy) / resolution.xy, light6.z );

    lightDir = normalize(deltaPos);
    lambert = useNormals ? clamp(dot(normal, lightDir), 0.0, 1.0) : 1.0;

    d = sqrt(dot(deltaPos, deltaPos));
    att = useShadow ? 1.0 / ( attenuation6.x + (attenuation6.y*d) + (attenuation6.z*d*d) ) : 1.0;

    result = (ambientColor * intensity6) + (lightColor6.rgb * lambert) * att;
    result *= color.rgb;
    vec4 resultG = v_color * vec4(result, color.a);
    // ///////////////////////////////////////////////////
    deltaPos = vec3( (light7.xy - gl_FragCoord.xy) / resolution.xy, light7.z );

    lightDir = normalize(deltaPos);
    lambert = useNormals ? clamp(dot(normal, lightDir), 0.0, 1.0) : 1.0;

    d = sqrt(dot(deltaPos, deltaPos));
    att = useShadow ? 1.0 / ( attenuation7.x + (attenuation7.y*d) + (attenuation7.z*d*d) ) : 1.0;

    result = (ambientColor * intensity7) + (lightColor7.rgb * lambert) * att;
    result *= color.rgb;
    vec4 resultH = v_color * vec4(result, color.a);
    // ///////////////////////////////////////////////////
    deltaPos = vec3( (light8.xy - gl_FragCoord.xy) / resolution.xy, light8.z );

    lightDir = normalize(deltaPos);
    lambert = useNormals ? clamp(dot(normal, lightDir), 0.0, 1.0) : 1.0;

    d = sqrt(dot(deltaPos, deltaPos));
    att = useShadow ? 1.0 / ( attenuation8.x + (attenuation8.y*d) + (attenuation8.z*d*d) ) : 1.0;

    result = (ambientColor * intensity8) + (lightColor8.rgb * lambert) * att;
    result *= color.rgb;
    vec4 resultI = v_color * vec4(result, color.a);
    // ///////////////////////////////////////////////////
    deltaPos = vec3( (light9.xy - gl_FragCoord.xy) / resolution.xy, light9.z );

    lightDir = normalize(deltaPos);
    lambert = useNormals ? clamp(dot(normal, lightDir), 0.0, 1.0) : 1.0;

    d = sqrt(dot(deltaPos, deltaPos));
    att = useShadow ? 1.0 / ( attenuation9.x + (attenuation9.y*d) + (attenuation9.z*d*d) ) : 1.0;

    result = (ambientColor * intensity9) + (lightColor9.rgb * lambert) * att;
    result *= color.rgb;
    vec4 resultJ = v_color * vec4(result, color.a);
    // ///////////////////////////////////////////////////

    gl_FragColor = resultA + resultB + resultC + resultD + resultE + resultF + resultG + resultH + resultI + resultJ;
}